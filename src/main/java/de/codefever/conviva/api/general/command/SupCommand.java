package de.codefever.conviva.api.general.command;

import de.codefever.conviva.api.general.prompt.SupPrompt;
import de.codefever.conviva.api.openai.ResponsesApiClient;
import de.codefever.conviva.model.general.Message;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        final List<String> preparedMessages = new ArrayList<>();
        for (final Message message : messages) {
            preparedMessages.add(String.format("%s : %s", message.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), message.getMessage()));
        }

        final SupPrompt prompt = new SupPrompt(preparedMessages);
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
