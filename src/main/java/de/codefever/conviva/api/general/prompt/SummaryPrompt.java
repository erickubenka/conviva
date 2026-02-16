package de.codefever.conviva.api.general.prompt;

import de.codefever.conviva.model.openai.Prompt;

import java.util.List;

/**
 * Prompt for summarizing chat messages grouped by topics and with bulletpoints.
 */
public class SummaryPrompt implements Prompt {

    private final List<String> messages;

    private static final String SYSTEM_PROMPT = "Du bist ein Assistent, welcher mir den Inhalt von Chatverläufen themenbasierend zusammenfasst und gruppiert.";
    private static final String USER_PROMPT = "Fasse den Chatverlauf zusammen. Gruppiere die Nachrichten nach Themen. Pro Thema MAXIMAL 3 Stichpunkte. Zeitstempel der ersten Nachricht des Thems angeben \"ab dd.MM.yyyy hh:mm Uhr\".";

    public SummaryPrompt(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Builds string with static prompt "Bitte fasse mir den folgenden Cahtverlauf zusammen, gruppiere dabei die Nachrichten nach Themen und gebe für jedes Thema die ungefähren Zeitstempel an."
     *
     * @return
     */
    @Override
    public String userPrompt() {

        final StringBuilder prompt = new StringBuilder(USER_PROMPT);
        for (final String message : messages) {
            prompt.append(message).append("\n");
        }

        return prompt.toString();
    }
}
