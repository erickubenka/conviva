package de.codefever.conviva.api.signal;

import de.codefever.conviva.api.signal.command.BotCommand;
import de.codefever.conviva.api.signal.command.GenericOpenAiAssistantCommand;
import de.codefever.conviva.model.signal.Group;
import de.codefever.conviva.model.signal.Message;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SignalApiBot implements Runnable, Loggable, PropertyManagerProvider {

    private static final Object LOCK = new Object();

    /**
     * Debug mode for the bot. If true, the bot will not send any messages and only log them.
     */
    private static final boolean DEBUG = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.debug");

    /**
     * Maximum cache time in hours for messages.
     */
    private static final int MAX_CACHE_TIME_IN_HOURS = Integer.parseInt(PROPERTY_MANAGER.getProperty("conviva.bot.cache.time"));

    /**
     * Name of the bot.
     */
    private static final String BOT_NAME = PROPERTY_MANAGER.getProperty("conviva.bot.name");

    /**
     * This shorthand will be used as command identifier to call the bot directly if you want to.
     */
    private static final String BOT_NAME_SHORTHAND = PROPERTY_MANAGER.getProperty("conviva.bot.name.shorthand");

    /**
     * Group ID where the bot is active and should listen to.
     */
    private static final String BOT_GROUP_ID = PROPERTY_MANAGER.getProperty("conviva.bot.signal.group.id");

    /**
     * Recipient of bot messages (group id or phone number).
     */
    private static final String BOT_RECIPIENT = PROPERTY_MANAGER.getProperty("conviva.bot.signal.recipient");


    /**
     * ExecutionContext of this bot instance.
     */
    private final ExecutionContext executionContext;
    private final Field methodContextLessLogs;

    /**
     * Start time of the bot.
     */
    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * List of threads the bot has started.
     */
    private final List<Thread> threads = new ArrayList<>();

    /**
     * List of commands the bot can execute.
     */
    private final List<BotCommand> commands = new ArrayList<>();

    /**
     * /**
     * List of messages the bot has read.
     */
    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    public SignalApiBot() {

        this.registerCommand(new GenericOpenAiAssistantCommand(BOT_NAME_SHORTHAND));

        try {
            this.executionContext = Testerra.getInjector().getInstance(IExecutionContextController.class).getExecutionContext();
            this.methodContextLessLogs = executionContext.getClass().getDeclaredField("methodContextLessLogs");
            this.methodContextLessLogs.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register a command for the bot.
     *
     * @param botCommand {@link BotCommand} to register.
     */
    public void registerCommand(final BotCommand botCommand) {
        log().info("Successfully registered command: {}", botCommand.command());
        this.commands.add(botCommand);
    }

    @Override
    public void run() {

        // TODO: Start up Sequence, may we should write messages to a file and read them on startup?
        // otherwise we miss all messages if the container was restarted.
        final SignalCliRestApiClient signalCliRestApiClient = new SignalCliRestApiClient();

        // fetch the internal id of the group.
        final List<Group> groups = signalCliRestApiClient.getGroups();
        final Group group = groups.stream().filter(g -> g.getId().equals(BOT_GROUP_ID)).findFirst().get();

        // init with all unread.
        final List<Message> initialMessages = signalCliRestApiClient.getLatestMessages();

        // filter for messages from the intended group only.
        initialMessages.removeIf(m -> {
            if (!m.isFromGroup()) {
                return true;
            } else {
                return !m.getGroupInfo().getGroupId().equals(group.getInternalId());
            }
        });

        this.messages.addAll(initialMessages);
        this.messages.removeIf(message -> message.getDateTime().isBefore(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS)));
        this.messages.sort(Comparator.comparing(Message::getDateTime));

        boolean stop = false;
        while (!stop) {
            final List<Message> latestMessages = signalCliRestApiClient.getLatestMessages();

            // new messages
            if (latestMessages != null && !latestMessages.isEmpty()) {
                log().info("Fetched {} new messages from Signal API.", latestMessages.size());
                this.messages.addAll(latestMessages);
                this.messages.removeIf(message -> message.getDateTime().isBefore(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS)));
                this.messages.sort(Comparator.comparing(Message::getDateTime));

                // iterate over all latest messages to check if a command was called.
                for (final Message newMessage : latestMessages) {
                    if (newMessage.getDateTime().isAfter(startTime)) {
                        for (final BotCommand command : this.commands) {
                            if (newMessage.getMessage() != null && newMessage.getMessage().trim().toLowerCase().startsWith(command.command())) {

                                // threaded commands ...
                                if (command.isRunInThread()) {
                                    final Thread thread = new Thread(() -> {
                                        log().info("Running Thread: {}", Thread.currentThread().getName());

                                        // anything to say before run a potential heavy-load command?
                                        if (!StringUtils.isBlank(command.beforeMessage())) {
                                            signalCliRestApiClient.postSendMessage(command.beforeMessage(), BOT_RECIPIENT);
                                        }

                                        // run command
                                        final String commandOutput = command.run(newMessage, this.filterMessages());
                                        if (!StringUtils.isBlank(commandOutput)) {
                                            signalCliRestApiClient.postSendMessage(command.outputIdentifier() + "\n" + commandOutput, BOT_RECIPIENT);
                                        }

                                        // anything to say after this command?
                                        if (!StringUtils.isBlank(command.afterMessage())) {
                                            signalCliRestApiClient.postSendMessage(command.afterMessage(), BOT_RECIPIENT);
                                        }
                                    });

                                    long sameCommandThreadCount = threads.stream().filter(t -> t.getName().equals(command.command())).count();
                                    thread.setName(command.command() + "-" + sameCommandThreadCount);
                                    threads.add(thread);
                                    thread.start();
                                }
                            }
                            break;
                        }
                    }
                }
            }

            messages.removeIf(message -> message.getDateTime().isBefore(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS)));
            threads.removeIf(thread -> !thread.isAlive());
            cleanUpLogs();
            final long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            final long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;

            log().info("Messages: {}    TotalMem: {}    FreeMem: {}     UsedMem: {}",
                    messages.size(),
                    totalMem,
                    freeMem,
                    totalMem - freeMem);
        }
    }

    /**
     * Filter messages based on registered commands to avoid sending them to other APIs
     *
     * @return filtered list of {@link de.codefever.conviva.model.signal.Message} that currently stored for this instance
     */
    private synchronized List<de.codefever.conviva.model.signal.Message> filterMessages() {

        final List<String> messagePartsToIgnore = new ArrayList<>();
        commands.forEach(command -> {

            if (!StringUtils.isBlank(command.outputIdentifier())) {
                messagePartsToIgnore.add(command.outputIdentifier());
            }

            if (!StringUtils.isBlank(command.command())) {
                messagePartsToIgnore.add(command.command());
            }

            if (!StringUtils.isBlank(command.beforeMessage())) {
                messagePartsToIgnore.add(command.beforeMessage());
            }

            if (!StringUtils.isBlank(command.afterMessage())) {
                messagePartsToIgnore.add(command.afterMessage());
            }
        });

        synchronized (LOCK) {
            return this.messages.stream().filter(Message::hasMessage).filter(message -> {
                for (final String part : messagePartsToIgnore) {
                    if (message.getMessage().contains(part)) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
        }
    }

    private void cleanUpLogs() {
        try {
            synchronized (LOCK) {
                ((ConcurrentLinkedQueue<LogMessage>) this.methodContextLessLogs.get(this.executionContext)).clear();
            }
        } catch (Exception e) {
            log().error("Error while cleaning up logs: {}", e.getMessage());
        }

        synchronized (LOCK) {
            Timings.TIMING_GUIELEMENT_FIND_WITH_PARENT.clear();
            Timings.TIMING_GUIELEMENT_FIND.clear();
        }
    }
}
