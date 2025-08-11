package de.codefever.conviva.api.openai;

import de.codefever.conviva.model.openai.Prompt;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Client for OpenAI API to get completions for given prompts.
 */
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

        final DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(API_URL);
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");

        StringEntity stringEntity = new StringEntity(jsonBody.toString(), StandardCharsets.UTF_8);
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            log().info("Response Code: " + httpResponse.getStatusLine().getStatusCode());

            BufferedReader in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            log().info("Response: " + response);
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("choices")) {
                log().info("Completion: " + jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));
                return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            } else {
                log().error("No completion found.");
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
