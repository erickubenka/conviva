package de.codefever.conviva.page.whatsapp;

import de.codefever.conviva.model.whatsapp.Message;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementList;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Page object for the chat page
 */
public class ChatPage extends HomePage {


    @Check
    private final UiElement inputChat = find(By.xpath("//div[@role='textbox' and @aria-label = 'Gib eine Nachricht ein.']"));

    private final UiElement buttonSend = find(By.xpath("//span[@data-icon='send']/..")); //

    public ChatPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Sends a message to the chat
     * Must be synchronized because of the send message can only be used by one thread at a time
     *
     * @param message {@link String}
     * @return {@link ChatPage}
     */
    public synchronized ChatPage sendMessage(final String message) {

        CONTROL.retryTimes(3, () -> {
            this.inputChat.clear();
            final Actions actions = new Actions(getWebDriver());
            if (message.contains("\n")) {
                final String[] lines = message.split("\n");
                for (String line : lines) {
                    actions.sendKeys(line);
                    actions.keyDown(Keys.SHIFT).sendKeys(Keys.ENTER).keyUp(Keys.SHIFT);
                }
                // actually send.
                actions.build().perform();
            } else {
                inputChat.sendKeys(message);
            }
        });

        CONTROL.waitFor(5, () -> buttonSend.expect().displayed());
        buttonSend.click();

        return createPage(ChatPage.class);
    }

    /**
     * Returns the last visible messages
     *
     * @param count {@link Integer}
     * @return {@link List} of {@link Message}
     */
    public List<Message> visibleMessages(final int count, final boolean includeOwnMessages) {

        final UiElement divMessageIn = find(By.cssSelector("div.message-in div.copyable-text"));
        final List<Message> messages = new ArrayList<>(this.readMessagesFromList(divMessageIn, count));

        if (includeOwnMessages) {
            UiElement divMessageOut = find(By.cssSelector("div.message-out div.copyable-text"));
            messages.addAll(this.readMessagesFromList(divMessageOut, count));
        }

        return messages;
    }

    /**
     * Returns all visible messages
     *
     * @return {@link List} of {@link Message}
     */
    public List<Message> visibleMessages(final boolean includeOwnMessages) {
        return this.visibleMessages(-1, includeOwnMessages);
    }

    /**
     * Scrolls up in chat window until first message is found that is older than the given {@link LocalDateTime}
     *
     * @param dateTime     {@link LocalDateTime}
     * @param startTimeout {@link Integer}
     * @return {@link List} of {@link Message}
     */
    public List<Message> allMessagesAfter(final LocalDateTime dateTime, final int startTimeout, final boolean includeOwnMessages) {

        final LocalDateTime startTime = LocalDateTime.now();
        while (LocalDateTime.now().isBefore(startTime.plusMinutes(startTimeout))) {
            final Message message = firstMessageOfList();
            if (message != null) {
                if (message.getDateTime().isBefore(dateTime)) {
                    break;
                }
                log().info("Latest message found: {}", message.getDateTime());
            }
            this.inputChat.sendKeys(Keys.PAGE_UP);
            TimerUtils.sleep(500);
        }

        final List<Message> visibleMessages = this.visibleMessages(includeOwnMessages);
        visibleMessages.sort(Comparator.comparing(Message::getDateTime));
        log().info("Found {} messages in {} seconds.", visibleMessages.size(), ChronoUnit.SECONDS.between(startTime, LocalDateTime.now()));
        return visibleMessages;
    }

    /**
     * Scrolls to the bottom of the chat window
     */
    public ChatPage scrollToBottom() {
        this.inputChat.sendKeys(Keys.PAGE_DOWN);
        TimerUtils.sleep(100);
        return createPage(ChatPage.class);
    }

    /**
     * Determines if given message div is displayed at least once, then run over the list to parse the given subelements into a valid {@link Message} object
     *
     * @param messageDiv {@link UiElement}
     * @return {@link List} of {@link Message}
     */
    private List<Message> readMessagesFromList(final UiElement messageDiv, int count) {

        final List<Message> messages = new ArrayList<>();
        if (messageDiv.expect().displayed().getActual()) {

            final UiElementList<UiElement> messageDivList = messageDiv.list();
            final int messageDivListSize = messageDivList.size();

            if (count == -1 || count > messageDivListSize) {
                count = messageDivListSize;
            }

            // if count is set to a specific number, run over the last count elements
            for (int i = messageDivListSize - count; i < messageDivListSize; i++) {
                log().info("Parsing message {}/{}.", i, count);
                final UiElement messageElement = messageDivList.get(i);
                final Message message = this.parseMessageFromUi(messageElement);
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        return messages;
    }

    /**
     * Returns the first message of the list
     *
     * @return {@link Message}
     */
    public Message firstMessageOfList() {
        final UiElement messageElement = find(By.cssSelector("div.message-in div.copyable-text"));
        final Message message = this.parseMessageFromUi(messageElement);

        if (message != null) {
            return message;
        }

        final UiElement messageElementOut = find(By.cssSelector("div.message-out div.copyable-text"));
        return this.parseMessageFromUi(messageElementOut);
    }

    /**
     * Returns the last message of the list
     *
     * @param includeOwnMessages {@link Boolean}
     * @return {@link Message}
     */
    public Message lastMessageOfList(final boolean includeOwnMessages) {
        final List<Message> foundMessages = this.visibleMessages(1, includeOwnMessages);
        if (!foundMessages.isEmpty()) {
            return foundMessages.get(0);
        }

        return null;
    }

    /**
     * Parses a message from a given {@link UiElement} into a {@link Message} object
     *
     * @param messageElement {@link UiElement}
     * @return {@link Message}
     */
    private Message parseMessageFromUi(final UiElement messageElement) {
        final UiElement copyableSpanElement = messageElement.find(By.cssSelector("span.copyable-text"));

        try {
            // does message have text? (For example single emoji messages does not have text)
            if (copyableSpanElement.expect().displayed().getActual()) {

                // div.message-in > div.copyable-text - Attribute data-pre-plain-text
                final String dateAndAuthor = messageElement.expect().attribute("data-pre-plain-text").getActual();

                // get span elements of message, if greater than one we have to combine them
                final UiElementList<UiElement> spanElementList = copyableSpanElement.list();
                final StringBuilder spanTextBuilder = new StringBuilder();
                for (final UiElement spanElement : spanElementList) {
                    spanTextBuilder.append(spanElement.expect().text().getActual());
                }

                // get text from message - this will exclude quoted messages
                final String textOfMessage = spanTextBuilder.toString();
                // found a message, go deeper...
                if (dateAndAuthor != null && !textOfMessage.isEmpty() && !dateAndAuthor.isEmpty()) {
                    try {
                        final Message message = new Message(dateAndAuthor, textOfMessage);

                        // quoted message detection - for !ggl or !tldr of a specific post. GitHub Issue #14
                        if (messageElement.find(By.cssSelector("span.quoted-mention")).expect().displayed().getActual()) {
                            final String quotedMessageText = messageElement.find(By.cssSelector("span.quoted-mention")).expect().text().getActual();
                            log().info(String.format("Quoted message found, message: %s, quoted message: %s", textOfMessage, quotedMessageText));
                            message.setQuotedMessage(quotedMessageText);
                        }

                        log().info("Message parsed successfully: {}", message);
                        return message;
                    } catch (Exception e) {
                        log().warn("Could not parse message: {} with date: {}", textOfMessage, dateAndAuthor);
                    }
                }
            }
        } catch (UiElementAssertionError e) {
            log().error("UiElementAssertionError: {}", messageElement);
        } catch (IndexOutOfBoundsException e) {
            log().error("IndexOutOfBoundsException: {}, {}", messageElement, e.getStackTrace());
        }

        return null;
    }
}
