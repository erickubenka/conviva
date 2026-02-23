package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.api.general.command.BotCommand;
import de.codefever.conviva.model.general.Message;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;

import java.util.List;

/**
 * Command to stop the bot.
 */
public class StopCommand implements BotCommand, WebDriverManagerProvider {

    private final String botName;

    public StopCommand(final String botName) {
        this.botName = botName;
    }

    @Override
    public String command() {
        return "!stopbot";
    }

    @Override
    public String description() {
        return "Stoppt den Bot - Nur im Notfall bitte.";
    }

    @Override
    public String outputIdentifier() {
        return "###STOP###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        if (this.isIntendedForQuotedMessage(callToCommand)) {
            return "";
        }

        TimerUtils.sleep(3000);
        WEB_DRIVER_MANAGER.shutdownAllSessions();
        System.exit(0);
        return "";
    }

    @Override
    public String beforeMessage() {
        return "Emergency Stop eingeleitet." +
                "\nCiao, Eure/r " + botName + ".";
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
        return false;
    }
}
