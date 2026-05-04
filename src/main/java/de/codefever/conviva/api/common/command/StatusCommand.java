package de.codefever.conviva.api.common.command;

import de.codefever.conviva.model.general.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command to display status of Bot
 */
public class StatusCommand implements BotCommand {

    private final LocalDateTime startTime;

    public StatusCommand(final LocalDateTime startTime) {
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
    public String run(final Message callToCommand, final List<Message> messages) {

        if (this.isIntendedForQuotedMessage(callToCommand)) {
            return "";
        }

        final String firstMessageTime =  messages.size() == 0 ? "n/a" : messages.get(0).getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        return "Online seit " + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ". " +
                "\nVerfügbare Nachrichten: " + messages.size() +
                "\nErste Nachricht von: " + firstMessageTime;
    }

    @Override
    public String beforeMessage() {
        return "";
    }

    @Override
    public String afterMessage() {
        return "";
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isRunInThread() {
        return true;
    }

}
