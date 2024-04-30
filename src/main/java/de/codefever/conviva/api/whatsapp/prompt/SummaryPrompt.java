package de.codefever.conviva.api.whatsapp.prompt;

import de.codefever.conviva.model.openai.Prompt;
import de.codefever.conviva.model.whatsapp.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Prompt for summarizing chat messages grouped by topics and with bulletpoints.
 */
public class SummaryPrompt implements Prompt {

    private final List<Message> messages;

    private static final String SYSTEM_PROMPT = "Du bist ein Assistent, welcher mir den Inhalt von Chatverläufen themenbasierend zusammenfasst und gruppiert.";
    private static final String USER_PROMPT = "Fasse den Chatverlauf zusammen. Gruppiere die Nachrichten nach Themen. Pro Thema MAXIMAL 3 Stichpunkte. Zeitstempel der ersten Nachricht des Thems angeben \"ab dd.MM.YYYY hh:mm Uhr\".";

    public SummaryPrompt(List<Message> messages) {
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
        for (Message message : messages) {
            prompt.append(String.format("%s : %s", message.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")), message.message)).append("\n");
        }

        return prompt.toString();
    }
}
