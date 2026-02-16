package de.codefever.conviva.api.whatsapp.command;


import de.codefever.conviva.api.general.prompt.SingleMessageSummaryPrompt;
import de.codefever.conviva.api.general.prompt.SummaryPrompt;
import de.codefever.conviva.api.openai.ResponsesApiClient;
import de.codefever.conviva.model.openai.Prompt;
import de.codefever.conviva.model.whatsapp.Message;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to summarize the chat history.
 */
public class TldrCommand implements BotCommand {

    @Override
    public String command() {
        return "!tldr";
    }

    @Override
    public String description() {
        return "Fasst den Chatverlauf oder die zitierte Nachricht zusammen.";
    }

    @Override
    public String outputIdentifier() {
        return "###SUMMARY###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        final List<String> preparedMessages = new ArrayList<>();
        for (final Message message : messages) {
            preparedMessages.add(String.format("%s : %s", message.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), message.getMessage()));
        }

        final Prompt prompt = this.isIntendedForQuotedMessage(callToCommand) ? new SingleMessageSummaryPrompt(callToCommand.getQuotedMessage()) : new SummaryPrompt(preparedMessages);
        log().info("Prompt: {}", prompt.userPrompt());
        final String summary = new ResponsesApiClient().postResponseRequest(prompt);
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
