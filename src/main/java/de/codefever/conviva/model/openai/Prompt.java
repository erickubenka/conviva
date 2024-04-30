package de.codefever.conviva.model.openai;

/**
 * Interface for prompt to OpenAI completions API with {@link de.codefever.conviva.api.openai.CompletionsApiClient}
 */
public interface Prompt {

    /**
     * Returns the system prompt for the OpenAI completions API.
     *
     * @return the system prompt
     */
    String systemPrompt();

    /**
     * Returns the user prompt for the OpenAI completions API.
     *
     * @return the user prompt
     */
    String userPrompt();
}
