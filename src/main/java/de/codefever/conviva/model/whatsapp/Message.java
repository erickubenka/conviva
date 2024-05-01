package de.codefever.conviva.model.whatsapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a message in a WhatsApp chat.
 */
public class Message {

    /**
     * The metadata of the message.
     */
    private final String metaData;

    /**
     * The message content.
     */
    private final String message;

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

    /**
     * Converts "[07:49, 4/19/2024] Author Name:" to date and author
     */
    private void parseMetaData() {

        final String[] split = metaData.split("]");
        final String date = split[0].substring(1);
        final String author = split[1].substring(1);

        try {
            this.dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("HH:mm, M/d/yyyy"));
        } catch (DateTimeParseException e) {
            this.dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("HH:mm, d.M.yyyy"));
        }
        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message && ((Message) obj).dateTime.equals(this.dateTime) && ((Message) obj).message.equals(message);
    }
}
