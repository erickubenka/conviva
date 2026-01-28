package de.codefever.conviva.api.openai;

import de.codefever.conviva.model.openai.Prompt;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Client for OpenAI API to get completions for given prompts.
 *
 * @deprecated - Please use {@link ResponsesApiClient} instead.
 */
@Deprecated(since = "2025-09-12")
public class CompletionsApiClient implements Loggable, PropertyManagerProvider {

    /**
     * OpenAI API URL and API key.
     */
    private final static String API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * OpenAI API key
     */
    private final static String API_KEY = PROPERTY_MANAGER.getProperty("conviva.openai.api.key");

    /**
     * OpenAI model to use for completions.
     */
    private final static String OPENAI_MODEL = PROPERTY_MANAGER.getProperty("conviva.openai.model", "gpt-4o");

    /**
     * Sends POST request to OpenAI API to get completion for given prompt.
     *
     * @param prompt {@link Prompt}
     * @return String
     */
    public String postCompletion(Prompt prompt) {

        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", OPENAI_MODEL);

        final JSONObject systemPromptMessageObject = new JSONObject();
        final JSONArray systemPromptContentArray = new JSONArray();
        final JSONObject systemPromptContentObject = new JSONObject();
        systemPromptMessageObject.put("role", "system");
        systemPromptContentObject.put("type", "text");
        systemPromptContentObject.put("text", prompt.systemPrompt());
        systemPromptContentArray.put(systemPromptContentObject);
        systemPromptMessageObject.put("content", systemPromptContentArray);

        final JSONObject userPromptMessageObject = new JSONObject();
        final JSONArray userPromptContentArray = new JSONArray();
        final JSONObject userPromptContentObject = new JSONObject();
        userPromptContentObject.put("type", "text");
        userPromptContentObject.put("text", prompt.userPrompt());
        userPromptContentArray.put(userPromptContentObject);
        userPromptMessageObject.put("role", "user");
        userPromptMessageObject.put("content", userPromptContentArray);

        final JSONArray messagesArray = new JSONArray();
        messagesArray.put(systemPromptMessageObject);
        messagesArray.put(userPromptMessageObject);
        jsonBody.put("messages", messagesArray);

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .header("Content-Encoding", "UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            final java.net.http.HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: " + httpResponse.statusCode());
            log().info("OpenAI Response: " + httpResponse.body());

            final JSONObject jsonResponse = new JSONObject(httpResponse.body());
            if (jsonResponse.has("choices")) {
                log().info("Completion: " + jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));
                return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            } else {
                log().error("No completion found.");
                return null;
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
