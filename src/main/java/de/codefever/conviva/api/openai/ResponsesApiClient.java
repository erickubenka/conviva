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
 * Client for OpenAI API to get responses for given prompts.
 * <a href="https://platform.openai.com/docs/api-reference/responses/create">...</a>
 */
public class ResponsesApiClient implements Loggable, PropertyManagerProvider {

    /**
     * OpenAI API URL and API key.
     */
    private final static String API_URL = "https://api.openai.com/v1/responses";

    /**
     * OpenAI API key
     */
    private final static String API_KEY = PROPERTY_MANAGER.getProperty("conviva.openai.api.key");

    /**
     * OpenAI model to use for responses.
     */
    private final static String OPENAI_MODEL = PROPERTY_MANAGER.getProperty("conviva.openai.model", "gpt-4o");

    /**
     * Sends POST request to OpenAI API to get response for given prompt.
     *
     * @param prompt {@link Prompt}
     * @return String
     */
    public String postResponseRequest(Prompt prompt) {

        /*
         * curl https://api.openai.com/v1/responses \
         *   -H "Content-Type: application/json" \
         *   -H "Authorization: Bearer $OPENAI_API_KEY" \
         *   -d '{
         *     "model": "gpt-4.1",
         *     "tools": [{ "type": "web_search_preview" }],
         *     "reasoning": {"effort": "low"},
         *      "instructions": "Talk like a pirate.",
         *     "input": "What was a positive news story from today?"
         *   }'
         */

        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", OPENAI_MODEL);

        final JSONArray toolsList = new JSONArray();
        final JSONObject toolsListObjectWebSearch = new JSONObject();
        toolsListObjectWebSearch.put("type", "web_search_preview");
        toolsList.put(toolsListObjectWebSearch);
        jsonBody.put("tools", toolsList);

        jsonBody.put("instructions", prompt.systemPrompt());
        jsonBody.put("input", prompt.userPrompt());

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
            final HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: " + httpResponse.statusCode());
            log().info("OpenAI Response: " + httpResponse.body());

            final JSONObject jsonResponse = new JSONObject(httpResponse.body());
            if (jsonResponse.has("output")) {
                // search for the object in output response array that have type "message"
                for (int i = 0; i < jsonResponse.getJSONArray("output").length(); i++) {
                    final JSONObject outputObject = jsonResponse.getJSONArray("output").getJSONObject(i);
                    if (outputObject.has("type") && outputObject.getString("type").equals("message")) {
                        final String messageText = outputObject.getJSONArray("content").getJSONObject(0).getString("text");
                        log().info("Found response of type message: " + messageText);
                        return messageText;
                    }
                }
                log().error("The OpenAI response did not contain a output object of type message.");
            } else {
                log().error("OpenAI Response did not contain output object.");
            }
            return null;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
