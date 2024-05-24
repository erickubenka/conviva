package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.model.whatsapp.Message;

import java.util.List;

/**
 * Command to display help.
 */
public class HelpCommand implements BotCommand {

    private final List<BotCommand> availableCommands;

    public HelpCommand(List<BotCommand> availableCommands) {
        this.availableCommands = availableCommands;
    }

    @Override
    public String command() {
        return "!help";
    }

    @Override
    public String description() {
        return "Zeigt diese Hilfe an.";
    }

    @Override
    public String outputIdentifier() {
        return "###HELP###";
    }

    @Override
    public String run(final List<Message> messages) {

        if (this.isIntendedForQuotedMessage(messages)) {
            return "";
        }

        final StringBuilder output = new StringBuilder("Folgende Befehle sind verfügbar:");
        for (final BotCommand command : availableCommands) {
            if (command.isPublic()) {
                output.append("\n").append(command.command()).append(" - ").append(command.description());
            }
        }

        return output.toString();
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
