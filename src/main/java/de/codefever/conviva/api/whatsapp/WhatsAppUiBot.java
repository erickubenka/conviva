package de.codefever.conviva.api.whatsapp;

import de.codefever.conviva.api.whatsapp.command.BotCommand;
import de.codefever.conviva.api.whatsapp.command.BugCommand;
import de.codefever.conviva.api.whatsapp.command.GenericOpenAiAssistantCommand;
import de.codefever.conviva.api.whatsapp.command.HelpCommand;
import de.codefever.conviva.api.whatsapp.command.RestartCommand;
import de.codefever.conviva.api.whatsapp.command.StatusCommand;
import de.codefever.conviva.api.whatsapp.command.StopCommand;
import de.codefever.conviva.api.whatsapp.command.SupCommand;
import de.codefever.conviva.api.whatsapp.command.TldrCommand;
import de.codefever.conviva.api.whatsapp.command.TopPostCommand;
import de.codefever.conviva.api.whatsapp.workflows.LoginWorkFlow;
import de.codefever.conviva.model.whatsapp.Message;
import de.codefever.conviva.page.whatsapp.ChatPage;
import de.codefever.conviva.page.whatsapp.HomePage;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Simple Bot that uses the WhatsApp Web UI to read and write messages in a specific group chat based on Testerra and Selenium Automation Framework.
 */
public class WhatsAppUiBot implements Runnable, Loggable, PageFactoryProvider, WebDriverManagerProvider, PropertyManagerProvider {

    private static final Object LOCK = new Object();

    /**
     * Debug mode for the bot. If true, the bot will not send any messages and only log them.
     */
    private static final boolean DEBUG = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.debug");

    /**
     * If true, the bot will read its own messages. This is useful for debugging purposes.
     */
    private static final boolean DEBUG_READ_OWN_MESSAGES = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.read.own.messages");

    /**
     * Timeout in minutes for the bot to start.
     */
    private static final int START_TIMEOUT = Integer.parseInt(PROPERTY_MANAGER.getProperty("conviva.bot.start.timeout"));

    /**
     * Name of the bot.
     */
    private static final String BOT_NAME = PROPERTY_MANAGER.getProperty("conviva.bot.name");

    /**
     * This shorthand will be used as command identifier to call the bot directly if you want to.
     */
    private static final String BOT_NAME_SHORTHAND = PROPERTY_MANAGER.getProperty("conviva.bot.name.shorthand");

    /**
     * Maximum cache time in hours for messages.
     */
    private static final int MAX_CACHE_TIME_IN_HOURS = Integer.parseInt(PROPERTY_MANAGER.getProperty("conviva.bot.cache.time"));

    /**
     * Name of the chat the bot should join.
     */
    private final String chatName;

    /**
     * UUID of the web driver.
     */
    private String webDriverUUID = null;

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
     * List of messages the bot has read.
     */
    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    /**
     * List of threads the bot has started.
     */
    private final List<Thread> threads = new ArrayList<>();

    /**
     * List of commands the bot can execute.
     */
    private final List<BotCommand> commands = new ArrayList<>();

    /**
     * Output identifier for the start message.
     */
    private static final String outputStart = "###START###";

    /**
     * Start with default chat name
     */
    public WhatsAppUiBot() {
        this(PROPERTY_MANAGER.getProperty("conviva.chat.name"));
    }

    /**
     * Start with custom chat name
     *
     * @param chatName Name of the chat the bot should join.
     */
    public WhatsAppUiBot(final String chatName) {
        this.chatName = chatName;
        this.registerCommand(new StatusCommand(startTime));
//        this.registerCommand(new StopCommand(BOT_NAME));
        this.registerCommand(new RestartCommand(this.chatName));
        this.registerCommand(new TldrCommand());
        this.registerCommand(new SupCommand());
        this.registerCommand(new BugCommand());
        this.registerCommand(new TopPostCommand());
        this.registerCommand(new GenericOpenAiAssistantCommand(BOT_NAME_SHORTHAND));
        this.registerCommand(new HelpCommand(this.commands));

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

    /**
     * Run the bot.
     */
    public void run() {

        log().info("Starting Bot {} in chat {} at {}.", BOT_NAME, chatName, startTime);
        this.webDriverUUID = WEB_DRIVER_MANAGER.makeExclusive(WEB_DRIVER_MANAGER.getWebDriver());
        ChatPage chatPage = new LoginWorkFlow(chatName, webDriverUUID).run();

        // init messages and get rid of old stuff.
        this.messages.addAll(chatPage.allMessagesAfter(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS), START_TIMEOUT, DEBUG_READ_OWN_MESSAGES));
        this.messages.removeIf(message -> message.getDateTime().isBefore(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS)));
        this.messages.sort(Comparator.comparing(Message::getDateTime));

        // reload for run process
        chatPage.getWebDriver().navigate().refresh();
        chatPage = chatPage.createPage(HomePage.class).openChat(chatName);

        // init done
        log().info("Initialized. Current messages for today: {}", messages.size());
        boolean stop = false;
        while (!stop) {
            this.messages.sort(Comparator.comparing(Message::getDateTime));
            final List<Message> potentiallyNewMessages = new ArrayList<>();

            try {
                chatPage.scrollToBottom();
                // break loop detection, so we have to go for a double class init to break buffer
                PAGE_FACTORY.createPage(HomePage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));
                chatPage = PAGE_FACTORY.createPage(ChatPage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));

                // find last message and check if already in message list
                // if we already know this message, just continue
                // if not, get last 5 messages to ensure we got everything that is possibly new.
                final Message message = chatPage.lastMessageOfList(DEBUG_READ_OWN_MESSAGES);
                if (message != null && !this.messages.contains(message)) {
                    potentiallyNewMessages.addAll(chatPage.visibleMessages(5, DEBUG_READ_OWN_MESSAGES));
                }
            } catch (Exception e) {
                log().error("Error while getting messages: {}, {}", e.getMessage(), e.getStackTrace());
                // Restart detection for Chrome when it comes unavailable - See GitHub Issue #15
                if (e.getMessage().contains("Could not create instance of HomePage on \"(na)\" ((na))")) {
                    log().error("WebDriver seems to be closed. Restarting WebDriver.");
                    WEB_DRIVER_MANAGER.shutdownAllSessions();
                    this.webDriverUUID = WEB_DRIVER_MANAGER.makeExclusive(WEB_DRIVER_MANAGER.getWebDriver());
                    new LoginWorkFlow(chatName, webDriverUUID).run();
                }
            }

            // if we have potentially new messages, check them
            this.messages.sort(Comparator.comparing(Message::getDateTime));
            if (!potentiallyNewMessages.isEmpty()) {
                for (final Message newMessage : potentiallyNewMessages) {
                    if (!messages.contains(newMessage)) {
                        messages.add(newMessage);
                        log().info("Added message to list: {}", newMessage.getMessage());
                        if (newMessage.getDateTime().isAfter(startTime)) {
                            for (final BotCommand command : this.commands) {
                                // check for registered commands and run.
                                if (newMessage.getMessage().trim().toLowerCase().startsWith(command.command())) {

                                    // threaded commands ...
                                    if (command.isRunInThread()) {
                                        final Thread thread = new Thread(() -> {
                                            log().info("Running Thread: {}", Thread.currentThread().getName());

                                            // anything to say before run a potential heavy-load command?
                                            if (!StringUtils.isBlank(command.beforeMessage())) {
                                                sendMessage(command.beforeMessage());
                                            }

                                            // For long quoted messages we search them in our own history to run commands with them
                                            // if the message is not in our own history, we just use the snippet we have
                                            if (newMessage.hasQuotedMessage()) {
                                                final Message quotedMessageInHistory = messages.stream().filter(message -> message.getMessage().contains(newMessage.getQuotedMessage())).findFirst().orElse(null);
                                                if (quotedMessageInHistory != null) {
                                                    newMessage.setQuotedMessage(quotedMessageInHistory.getMessage());
                                                }
                                            }

                                            // run command
                                            final String commandOutput = command.run(newMessage, this.filterMessages());
                                            if (!StringUtils.isBlank(commandOutput)) {
                                                sendMessage(command.outputIdentifier() + "\n" + commandOutput);
                                            }

                                            // anything to say after this command?
                                            if (!StringUtils.isBlank(command.afterMessage())) {
                                                sendMessage(command.afterMessage());
                                            }
                                        });

                                        thread.setName(command.command());
                                        threads.add(thread);
                                        thread.start();
                                    }

                                    // non-threaded commands like restart, stop, etc.
                                    if (!command.isRunInThread()) {

                                        while (threads.stream().anyMatch(Thread::isAlive)) {
                                            TimerUtils.sleep(1000, "Waiting for other threads to finish.");
                                        }

                                        // StopCommand extra definition
                                        if (command instanceof StopCommand) {
                                            stop = true;
                                        }

                                        // anything to say before run a potential heavy-load command?
                                        if (StringUtils.isBlank(command.beforeMessage())) {
                                            sendMessage(command.beforeMessage());
                                        }

                                        // run command
                                        String commandOutput = command.run(newMessage, this.filterMessages());
                                        // RestartCommand extra definition
                                        if (command instanceof RestartCommand) {
                                            this.webDriverUUID = commandOutput;
                                            commandOutput = "";
                                        }

                                        if (!StringUtils.isBlank(commandOutput)) {
                                            sendMessage(command.outputIdentifier() + "\n" + commandOutput);
                                        }

                                        // anything to say after this command?
                                        if (!StringUtils.isBlank(command.afterMessage())) {
                                            sendMessage(command.afterMessage());
                                        }
                                    }

                                    // exit for loop when command found early.
                                    break;
                                }
                            }

                            // highlight - todo - for future commands
                            if (newMessage.getMessage().contains("@" + BOT_NAME)) {
                                log().info("Highlight: {}", newMessage.getMessage());
                            }
                        } else {
                            log().debug("Did not try to parse message into a bot command because it was sent {} before the bot started {}.", newMessage.getDateTime(), startTime);
                        }
                    }
                }
            }

            // cleanup
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
     * Send a message to the chat.
     * Must be synchronized because sendMessage input can only be used once at a time.
     *
     * @param message {@link Message} to send.
     * @return Refreshed {@link ChatPage} instance of the chat the bot joined.
     */
    private synchronized ChatPage sendMessage(final String message) {

        final ChatPage chatPage = PAGE_FACTORY.createPage(ChatPage.class, WEB_DRIVER_MANAGER.getWebDriver(this.webDriverUUID));

        if (DEBUG) {
            log().info("DEBUG: {}", message);
            return chatPage;
        }

        return chatPage.sendMessage(message);
    }

    /**
     * Filter messages based on registered commands to avoid sending them to other APIs
     *
     * @return filtered list of {@link Message} that currently stored for this instance
     */
    private synchronized List<Message> filterMessages() {

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
            return this.messages.stream().filter(message -> {
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
