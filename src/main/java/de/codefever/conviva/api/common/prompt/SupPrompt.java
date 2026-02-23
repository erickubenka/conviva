package de.codefever.conviva.api.common.prompt;

import de.codefever.conviva.model.openai.Prompt;

import java.util.List;

/**
 * Prompt for summarizing chat messages grouped by topics without bulletpoints.
 */
public class SupPrompt implements Prompt {

    private final List<String> messages;

    private static final String SYSTEM_PROMPT = "Du bist ein Assistent, welcher mir den Inhalt von Chatverläufen themenbasierend zusammenfasst und gruppiert.";
    private static final String USER_PROMPT = "Fasse den Chatverlauf thematisch zusammen und gib mir die Themen als Bulletpoint Liste im Signal Format in chronologischer Reihenfolge mit Timestamp des Beginns des Themas aus: \"ab dd.MM.yyyy hh:mm Uhr\".";

    public SupPrompt(final List<String> messages) {
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
        for (String message : messages) {
            prompt.append(message).append("\n");
        }

        return prompt.toString();
    }
}
