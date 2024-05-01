package de.codefever.conviva.api.whatsapp;

import de.codefever.conviva.api.whatsapp.command.BotCommand;
import de.codefever.conviva.api.whatsapp.command.BugCommand;
import de.codefever.conviva.api.whatsapp.command.HelpCommand;
import de.codefever.conviva.api.whatsapp.command.StatusCommand;
import de.codefever.conviva.api.whatsapp.command.StopCommand;
import de.codefever.conviva.api.whatsapp.command.SummaryCommand;
import de.codefever.conviva.api.whatsapp.command.SupCommand;
import de.codefever.conviva.api.whatsapp.command.TldrCommand;
import de.codefever.conviva.api.whatsapp.command.TopPostCommand;
import de.codefever.conviva.model.whatsapp.Message;
import de.codefever.conviva.page.whatsapp.ChatPage;
import de.codefever.conviva.page.whatsapp.ConnectWithNumberPage;
import de.codefever.conviva.page.whatsapp.HomePage;
import de.codefever.conviva.page.whatsapp.LoginPage;
import de.codefever.conviva.page.whatsapp.PhoneNumberVerificationPage;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple Bot that uses the WhatsApp Web UI to read and write messages in a specific group chat based on Testerra and Selenium Automation Framework.
 */
public class WhatsAppUiBot implements Runnable, Loggable, PageFactoryProvider, WebDriverManagerProvider, PropertyManagerProvider {

    /**
     * Debug mode for the bot. If true, the bot will not send any messages and only log them.
     */
    private static final boolean DEBUG = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.debug");

    /**
     * If true, the bot will read its own messages. This is useful for debugging purposes.
     */
    private static final boolean DEBUG_READ_OWN_MESSAGES = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.read.own.messages");

    /**
     * If true, the bot will start silently without any message.
     */
    private static final boolean START_SILENT = PROPERTY_MANAGER.getBooleanProperty("conviva.bot.start.silent");

    /**
     * Timeout in minutes for the bot to start.
     */
    private static final int START_TIMEOUT = Integer.parseInt(PROPERTY_MANAGER.getProperty("conviva.bot.start.timeout"));

    /**
     * Name of the bot.
     */
    private static final String BOT_NAME = PROPERTY_MANAGER.getProperty("conviva.bot.name");

    /**
     * Maximum cache time in hours for messages.
     */
    private static final int MAX_CACHE_TIME_IN_HOURS = Integer.parseInt(PROPERTY_MANAGER.getProperty("conviva.bot.cache.time"));

    /**
     * Name of the chat the bot should join.
     */
    private final String chatName;

    /**
     * Start time of the bot.
     */
    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * List of messages the bot has read.
     */
    private List<Message> messages = new ArrayList<>();

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
        this.registerCommand(new StopCommand(BOT_NAME));
        this.registerCommand(new SummaryCommand());
        this.registerCommand(new TldrCommand());
        this.registerCommand(new SupCommand());
        this.registerCommand(new BugCommand());
        this.registerCommand(new TopPostCommand());
        this.registerCommand(new HelpCommand(this.commands));
    }

    /**
     * Register a command for the bot.
     *
     * @param botCommand {@link BotCommand} to register.
     */
    public void registerCommand(BotCommand botCommand) {
        log().info("Successfully registered command: " + botCommand.command());
        this.commands.add(botCommand);
    }

    /**
     * Run the bot.
     */
    public void run() {

        log().info("Starting Bot " + BOT_NAME);
        ChatPage chatPage = this.performLogin();

        // init messages
        this.messages = chatPage.allMessagesAfter(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS), START_TIMEOUT, DEBUG_READ_OWN_MESSAGES);
        this.messages = filterMessages(this.messages);
        this.messages.sort(Comparator.comparing(Message::getDateTime));

        // reload for run process
        chatPage.getWebDriver().navigate().refresh();
        HomePage homePage = chatPage.createPage(HomePage.class);
        chatPage = homePage.openChat(chatName);
        chatPage.scrollToBottom();

        // init done
        log().info("Initialized. Current messages for today: " + messages.size());

        if (!START_SILENT) {
            this.sendMessage(chatPage, outputStart +
                    "\nHi, ich bin " + BOT_NAME + " und jetzt verfügbar." +
                    "\nIch kann dir " + filterMessages(this.messages).size() + " Nachrichten seit " + filterMessages(this.messages).get(0).getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " zusammenfassen. " +
                    "\nSchreibe !help für Hilfe.");
        }

        boolean stop = false;
        while (!stop) {
            // sort
            this.messages.sort(Comparator.comparing(Message::getDateTime));

            List<Message> newMessages = new ArrayList<>();
            try {
                newMessages = chatPage.visibleMessages(5, DEBUG_READ_OWN_MESSAGES);
                // break loop detection, so we have to go for a double class iniet to break buffer
                PAGE_FACTORY.createPage(HomePage.class);
                chatPage = PAGE_FACTORY.createPage(ChatPage.class);
            } catch (Exception e) {
                log().error("Error while getting messages: " + e.getMessage());
            }

            for (Message newMessage : newMessages) {
                if (!messages.contains(newMessage)) {

                    // add new message to history
                    messages.add(newMessage);
                    log().info("Added message to list: " + newMessage.getMessage());

                    if (newMessage.getDateTime().isAfter(startTime)) {
                        for (final BotCommand command : this.commands) {
                            // check for registered commands and run.
                            if (newMessage.getMessage().trim().toLowerCase().equals(command.command())) {

                                // StopCommand extra definition
                                if (command instanceof StopCommand) {
                                    stop = true;
                                }

                                ChatPage finalChatPage = chatPage;
                                Thread thread = new Thread(() -> {
                                    log().info("Running Thread: {}", Thread.currentThread().getName());

                                    // anything to say before run a potential hevy-load command?
                                    if (command.beforeMessage() != null && !command.beforeMessage().isEmpty()) {
                                        sendMessage(finalChatPage, command.beforeMessage());
                                    }

                                    // run command
                                    final String commandOutput = command.run(filterMessages(messages));
                                    sendMessage(finalChatPage, command.outputIdentifier() + "\n" + commandOutput);

                                    // anything to say after this command?
                                    if (command.afterMessage() != null && !command.afterMessage().isEmpty()) {
                                        sendMessage(finalChatPage, command.afterMessage());
                                    }
                                });

                                thread.setName(command.command());
                                thread.start();

                                // exit for loop when command found early.
                                break;
                            }
                        }

                        // highlight - todo - for future commands
                        if (newMessage.getMessage().contains("@" + BOT_NAME)) {
                            log().info("Highlight: " + newMessage.getMessage());
                        }
                    }
                }
            }

            // cleanup
            messages.removeIf(message -> message.getDateTime().isBefore(LocalDateTime.now().minusHours(MAX_CACHE_TIME_IN_HOURS)));
//            System.gc();
            JVMMonitor.getMeasurements();
            log().info(JVMMonitor.getJVMUsageInfo());
            TimerUtils.sleep(250);
        }
    }

    /**
     * Perform the login process based on specified options.
     *
     * @return {@link ChatPage} instance of the chat the bot joined.
     */
    private ChatPage performLogin() {

        final LoginPage loginPage = PAGE_FACTORY.createPage(LoginPage.class);

        if (PROPERTY_MANAGER.getProperty("conviva.auth.mode").equals("phone")) {
            ConnectWithNumberPage connectWithNumberPage = loginPage.goToConnectWithNumberPage();
            connectWithNumberPage = connectWithNumberPage.selectCountry(PROPERTY_MANAGER.getProperty("conviva.auth.phone.country"));
            PhoneNumberVerificationPage phoneNumberVerificationPage = connectWithNumberPage.connectWithNumber(PROPERTY_MANAGER.getProperty("conviva.auth.phone.number"));
            final HomePage homePage = phoneNumberVerificationPage.waitForNumberVerified();
            return homePage.openChat(this.chatName);
        } else {
            final HomePage homePage = loginPage.waitForQrCodeScanned();
            return homePage.openChat(this.chatName);
        }
    }

    /**
     * Send a message to the chat.
     * Must be synchronized because sendMessage input can only be used once at a time.
     *
     * @param chatPage {@link ChatPage} instance of the chat the bot joined.
     * @param message  {@link Message} to send.
     * @return Refreshed {@link ChatPage} instance of the chat the bot joined.
     */
    private synchronized ChatPage sendMessage(ChatPage chatPage, final String message) {

        if (DEBUG) {
            log().info("DEBUG: " + message);
            return chatPage;
        }

        chatPage = chatPage.sendMessage(message);
        return chatPage;
    }

    /**
     * Filter messages based on registered commands to avoid sending them to other APIs
     *
     * @param unfilteredList of {@link Message}
     * @return filtered list of {@link Message}
     */
    private List<Message> filterMessages(final List<Message> unfilteredList) {

        final List<String> messagePartsToIgnore = new ArrayList<>();
        commands.forEach(command -> {
            messagePartsToIgnore.add(command.outputIdentifier());
            messagePartsToIgnore.add(command.command());

            if (command.beforeMessage() != null && !command.beforeMessage().isEmpty()) {
                messagePartsToIgnore.add(command.beforeMessage());
            }

            if (command.afterMessage() != null && !command.afterMessage().isEmpty()) {
                messagePartsToIgnore.add(command.afterMessage());
            }
        });

        return unfilteredList.stream().filter(message -> {
            for (final String part : messagePartsToIgnore) {
                if (message.getMessage().contains(part)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
