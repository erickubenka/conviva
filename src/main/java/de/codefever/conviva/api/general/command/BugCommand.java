package de.codefever.conviva.api.general.command;

import de.codefever.conviva.model.general.Message;

import java.util.List;

/**
 * Command to report a bug.
 */
public class BugCommand implements BotCommand {

    @Override
    public String command() {
        return "!bug";
    }

    @Override
    public String description() {
        return "Fehler melden";
    }

    @Override
    public String outputIdentifier() {
        return "###BUG###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {
        return "Meldung erfolgreich. Für schnelle Bearbeitung hilft eine Spende an paypal.me/erickubenka";
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
        return false;
    }

    @Override
    public boolean isRunInThread() {
        return true;
    }
}
