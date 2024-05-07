package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.api.openai.CompletionsApiClient;
import de.codefever.conviva.api.whatsapp.prompt.SummaryPrompt;
import de.codefever.conviva.model.whatsapp.Message;

import java.util.List;

/**
 * Command to summarize the chat history.
 */
public class SummaryCommand implements BotCommand {

    @Override
    public String command() {
        return "!zusammenfassung";
    }

    @Override
    public String description() {
        return "Fasst den Chatverlauf zusammen.";
    }

    @Override
    public String outputIdentifier() {
        return "###SUMMARY###";
    }

    @Override
    public String run(final List<Message> messages) {

        final SummaryPrompt prompt = new SummaryPrompt(messages);
        log().info("Prompt: {}", prompt.userPrompt());
        final String summary = new CompletionsApiClient().postCompletion(prompt);
        log().info("Summary: {}", summary);
        return summary;
    }

    @Override
    public String beforeMessage() {
        return "###SUMMARY IN PROCESS###";
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
