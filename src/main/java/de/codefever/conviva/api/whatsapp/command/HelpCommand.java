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
    public String run(List<Message> messages) {

        final StringBuilder output = new StringBuilder("Folgende Befehle sind verfügbar:");
        output.append("\n").append(command()).append(" - ").append(description());
        for (BotCommand command : availableCommands) {
            output.append("\n").append(command.command()).append(" - ").append(command.description());
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
}
