package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.model.whatsapp.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command to display status of Bot
 */
public class StatusCommand implements BotCommand {

    private final LocalDateTime startTime;

    public StatusCommand(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String command() {
        return "!status";
    }

    @Override
    public String description() {
        return "Zeigt den Status des Bots an.";
    }

    @Override
    public String outputIdentifier() {
        return "###STATUS###";
    }

    @Override
    public String run(List<Message> messages) {
        return "Online seit " + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ". " +
                "\nIch kann dir " + messages.size() + " Nachrichten seit " + messages.get(0).getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " zusammenfassen. ";
    }

    @Override
    public String beforeMessage() {
        return "";
    }

    @Override
    public String afterMessage() {
        return "";
    }

}
