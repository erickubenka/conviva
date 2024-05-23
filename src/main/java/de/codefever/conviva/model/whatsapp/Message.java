package de.codefever.conviva.model.whatsapp;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents a message in a WhatsApp chat.
 */
public class Message implements PropertyManagerProvider {

    private static final ZoneId MESSAGE_TIME_ZONE_ID = ZoneId.of(PROPERTY_MANAGER.getProperty("conviva.bot.timezone.messages"));
    private static final ZoneId TARGET_TIME_ZONE_ID = ZoneId.of(PROPERTY_MANAGER.getProperty("conviva.bot.timezone.target"));

    /**
     * The metadata of the message.
     */
    private final String metaData;

    /**
     * The message content.
     */
    private final String message;

    /**
     * Content of quoted message
     */
    private String quotedMessage;

    /**
     * The date and time of the message.
     */
    private LocalDateTime dateTime;

    /**
     * The author of the message.
     */
    private String author;

    /**
     * Creates a new message.
     *
     * @param metaData the metadata of the message
     * @param message  the message content
     */
    public Message(final String metaData, final String message) {
        this.metaData = metaData;
        this.message = message;
        parseMetaData();
    }

    public String getMetaData() {
        return metaData;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getQuotedMessage() {
        return quotedMessage;
    }

    public void setQuotedMessage(final String quotedMessage) {
        this.quotedMessage = quotedMessage;
    }

    /**
     * Converts "[07:49, 4/19/2024] Author Name:" to date and author
     */
    private void parseMetaData() {

        final String[] split = metaData.split("]");
        final String date = split[0].substring(1);
        final String author = split[1].substring(1);

        try {
            this.dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("HH:mm, M/d/yyyy"));
        } catch (final DateTimeParseException e) {
            this.dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("HH:mm, d.M.yyyy"));
        }

        // transform if necessary, take the dateTime and suggest it is in UTC timezone, then transform it to the Europe/Berlin timezone
        if (!MESSAGE_TIME_ZONE_ID.equals(TARGET_TIME_ZONE_ID)) {
            this.dateTime = this.dateTime.atZone(MESSAGE_TIME_ZONE_ID).withZoneSameInstant(TARGET_TIME_ZONE_ID).toLocalDateTime();
        }

        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message && ((Message) obj).dateTime.equals(this.dateTime) && ((Message) obj).message.equals(message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "metaData='" + metaData + '\'' +
                ", message='" + message + '\'' +
                ", dateTime=" + dateTime + '\'' +
                ", quotedMessage=" + quotedMessage + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, dateTime);
    }
}
