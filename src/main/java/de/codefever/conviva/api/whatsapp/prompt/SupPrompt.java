package de.codefever.conviva.api.whatsapp.prompt;

import de.codefever.conviva.model.openai.Prompt;
import de.codefever.conviva.model.whatsapp.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Prompt for summarizing chat messages grouped by topics without bulletpoints.
 */
public class SupPrompt implements Prompt {

    private final List<Message> messages;

    private static final String SYSTEM_PROMPT = "Du bist ein Assistent, welcher mir den Inhalt von Chatverläufen themenbasierend zusammenfasst und gruppiert.";
    private static final String USER_PROMPT = "Fasse den Chatverlauf thematisch zusammen und gib mir die Themen als Bulletpoint Liste im WhatsApp Format in chronologischer Reihenfolge mit Timestamp des Beginns des Themas aus: \"ab dd.MM.yyyy hh:mm Uhr\".";

    public SupPrompt(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Builds string with static prompt "Fasse den Chatverlauf thematisch zusammen und gib mir die Themen als Bulletpoint Liste im WhatsApp Format in chronologischer Reihenfolge mit Timestamp des Beginn s des Themas aus: \"ab dd.MM.yyyy hh:mm Uhr\".""
     *
     * @return
     */
    @Override
    public String userPrompt() {

        final StringBuilder prompt = new StringBuilder(USER_PROMPT);
        for (Message message : messages) {
            prompt.append(String.format("%s : %s", message.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), message.getMessage())).append("\n");
        }

        return prompt.toString();
    }
}
