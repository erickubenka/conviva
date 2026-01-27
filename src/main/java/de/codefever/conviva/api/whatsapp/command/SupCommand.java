package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.api.general.prompt.SupPrompt;
import de.codefever.conviva.api.openai.ResponsesApiClient;
import de.codefever.conviva.model.whatsapp.Message;

import java.util.List;

/**
 * Command to summarize a chat history to a short summary.
 */
public class SupCommand implements BotCommand {

    @Override
    public String command() {
        return "!sup";
    }

    @Override
    public String description() {
        return "Richtig kurze Zusammenfassung.";
    }

    @Override
    public String outputIdentifier() {
        return "###SUP###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        if (this.isIntendedForQuotedMessage(callToCommand)) {
            return "";
        }

        final SupPrompt prompt = new SupPrompt(messages);
        log().info("Prompt: {}", prompt.userPrompt());
        final String summary = new ResponsesApiClient().postResponseRequest(prompt);
        log().info("SUP: {}", summary);
        return summary;
    }

    @Override
    public String beforeMessage() {
        return "###SUP IN PROCESS###";
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
