package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.api.general.command.BotCommand;
import de.codefever.conviva.api.whatsapp.workflows.LoginWorkFlow;
import de.codefever.conviva.model.general.Message;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;

import java.util.List;

public class RestartCommand implements BotCommand, WebDriverManagerProvider, PageFactoryProvider {

    private final String chatName;

    public RestartCommand(final String chatName) {
        this.chatName = chatName;
    }

    @Override
    public String command() {
        return "!restart";
    }

    @Override
    public String description() {
        return "Startet die Browser Session neu.";
    }

    @Override
    public String outputIdentifier() {
        return "";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        TimerUtils.sleep(5000, "Wait for Restart!");
        WEB_DRIVER_MANAGER.shutdownAllSessions();
        TimerUtils.sleep(5000, "Wait for Restart!");
        final String webDriverUUID = WEB_DRIVER_MANAGER.makeExclusive(WEB_DRIVER_MANAGER.getWebDriver());
        new LoginWorkFlow(chatName, webDriverUUID).run();
        return webDriverUUID;
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
        return false;
    }
}
