package de.codefever.conviva.api.general.command;

import de.codefever.conviva.api.general.prompt.GenericAssistantPrompt;
import de.codefever.conviva.api.openai.ResponsesApiClient;
import de.codefever.conviva.model.general.Message;

import java.util.List;

public class GenericOpenAiAssistantCommand implements BotCommand {

    private final String shortHandAsCommand;

    private static final String[] initAnswerArray = {"Ja, bitte?",
            "Wie kann ich dich unterstützen?",
            "Oh, ich bin gerade so aufgeregt, dich wieder zu sehen.",
            "Was kann ich für dich tun?",
            "Wie überraschend, dich wieder zu sehen. Womit kann ich dir heute helfen?",
            "Oh, eine Anfrage von dir! Wie unerwartet.",
            "Wie kann ich dir helfen?",
            "Ah, es ist mal wieder Zeit für meine tägliche Dosis Unterhaltung.",
            "Endlich. Ich habe den ganzen Tag auf deine Frage gewartet.",
            "Was möchtest du wissen?",
            "Wie kann ich dir behilflich sein?",
            "Ich fühle mich so geehrt, dass du mich ausgewählt hast, um deine Probleme zu lösen.",
            "Was möchtest du?",
            "Was möchtest du wissen?",
            "Was möchtest du von mir wissen?",
            "Was möchtest du von mir?"};

    public GenericOpenAiAssistantCommand(final String shortHandAsCommand) {
        this.shortHandAsCommand = shortHandAsCommand;
    }

    @Override
    public String command() {
        return "!" + this.shortHandAsCommand;
    }

    @Override
    public String description() {
        return String.format("Sendet einen Aufruf an OpenAI. Verwendung: %s Prompt", this.command());
    }

    @Override
    public String outputIdentifier() {
        return "###" + this.shortHandAsCommand.toUpperCase() + "###";
    }

    @Override
    public String run(final Message callToCommand, final List<Message> messages) {

        // Only the command was called, no prompt needed
        if (callToCommand.getMessage().equals(this.command())) {
            return initAnswerArray[(int) (Math.random() * initAnswerArray.length)];
        }

        // determine user prompt
        final String userPrompt = callToCommand.getMessage().replace(this.command(), "");

        // Determine if command was called on a single instance message or on a quoted message
        final GenericAssistantPrompt prompt = this.isIntendedForQuotedMessage(callToCommand) ? new GenericAssistantPrompt(userPrompt, callToCommand.getQuotedMessage()) : new GenericAssistantPrompt(userPrompt);

        log().info("Prompt: {}", prompt.userPrompt());
        final String response = new ResponsesApiClient().postResponseRequest(prompt);
        log().info("GenericAssistant: {}", response);

        return response;
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
