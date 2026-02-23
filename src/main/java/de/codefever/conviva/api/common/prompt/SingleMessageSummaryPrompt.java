package de.codefever.conviva.api.common.prompt;

import de.codefever.conviva.model.openai.Prompt;

public class SingleMessageSummaryPrompt implements Prompt {

    private final String message;

    private static final String SYSTEM_PROMPT = "Du bist ein Assistent, welcher mir eine Nachricht auf die Kernaussagen zusammenfasst.";
    private static final String USER_PROMPT = "Fasse folgende Nachricht auf ihre Kernaussagen zusammen:";

    public SingleMessageSummaryPrompt(final String message) {
        this.message = message;
    }

    @Override
    public String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String userPrompt() {
        return USER_PROMPT + "\n" + "\"" + this.message + "\"";
    }
}
