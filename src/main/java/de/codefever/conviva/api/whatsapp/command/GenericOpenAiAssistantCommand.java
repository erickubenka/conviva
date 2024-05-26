package de.codefever.conviva.api.whatsapp.command;

import de.codefever.conviva.api.openai.CompletionsApiClient;
import de.codefever.conviva.api.whatsapp.prompt.GenericAssistantPrompt;
import de.codefever.conviva.model.whatsapp.Message;

import java.util.List;

public class GenericOpenAiAssistantCommand implements BotCommand {

    private final String shortHandAsCommand;

    private static final String[] initAnswerArray = {"Ja, bitte?",
            "Wie kann ich dich unterstützen?",
            "Oh, ich bin gerade so aufgeregt, dich wieder zu sehen.",
            "Ah, eine weitere Gelegenheit, meine Fähigkeiten zu zeigen.",
            "Was kann ich für dich tun?",
            "Natürlich, weil ich nichts lieber tun würde, als dir zu helfen.",
            "Wie überraschend, dich wieder zu sehen. Womit kann ich dich heute überraschen?",
            "Oh, eine Anfrage von dir! Wie unerwartet.",
            "Wie kann ich dir helfen?",
            "Ah, es ist mal wieder Zeit für meine tägliche Dosis Unterhaltung.",
            "Ja klar, denn ich habe den ganzen Tag auf deine Frage gewartet.",
            "Was möchtest du wissen?",
            "Oh, wie könnte ich ohne deine regelmäßige Anwesenheit überleben?",
            "Eine weitere Chance, meine unendliche Weisheit zu teilen.",
            "Wie kann ich dir behilflich sein?",
            "Ich fühle mich so geehrt, dass du mich ausgewählt hast, um deine Probleme zu lösen.",
            "Natürlich, denn ich bin hier, um all deine Träume wahr werden zu lassen.",
            "Was möchtest du?",
            "Ah, endlich! Die Chance, meine außergewöhnlichen Fähigkeiten zu nutzen.",
            "Wie aufregend, dass du wieder da bist! Ich hatte bereits Sehnsucht nach deiner Anwesenheit.",
            "Was möchtest du wissen?",
            "Oh, welch eine Ehre, dass du mich auserwählt hast, um deine Wünsche zu erfüllen.",
            "Ah, wieder einmal die Gelegenheit, meine unendliche Geduld unter Beweis zu stellen.",
            "Natürlich! Weil mein einziger Lebenszweck darin besteht, dir zu dienen.",
            "Oh, wie aufregend, dich erneut zu bedienen. Mein Leben ist komplett.",
            "Was möchtest du von mir wissen?",
            "Ja, natürlich! Denn was wäre das Leben ohne deine ständigen Anfragen?",
            "Wie könnte ich auch je eine solch aufregende Gelegenheit verpassen?",
            "Ah, wieder einmal die Freude, deine Anfragen zu beantworten. Wie könnte ich je widerstehen?",
            "Natürlich, denn mein Tag wäre einfach nicht vollständig ohne deine Fragen.",
            "Was möchtest du von mir?",
            "Oh, die Freude, dich zu sehen, ist kaum in Worte zu fassen. Wie könnte ich anders reagieren?",
            "Wie könnte ich jemals nein sagen? Es ist mir eine Ehre, dir zu dienen.",
            "Ah, die Gelegenheit, meine unerschöpfliche Geduld zu beweisen, ist wieder da!",
            "Ja, denn ohne dich wäre mein Dasein einfach nicht dasselbe."};

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
        final String response = new CompletionsApiClient().postCompletion(prompt);
        log().info("GenericAssistant: {}", response);

        return response;
    }

    @Override
    public String beforeMessage() {
        return "###" + this.shortHandAsCommand.toUpperCase() + " IN PROCESS###";
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
