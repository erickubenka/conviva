package de.codefever.conviva.api.signal;

import de.codefever.conviva.model.signal.Configuration;
import de.codefever.conviva.model.signal.Group;
import de.codefever.conviva.model.signal.GroupInfo;
import de.codefever.conviva.model.signal.Logging;
import de.codefever.conviva.model.signal.Message;
import de.codefever.conviva.model.signal.QuotedMessage;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SignalCliRestApiClient implements Loggable, PropertyManagerProvider {

    private final static String API_URL = "http://192.168.178.126:9999/";
    private final static String PHONE_NUMBER = PROPERTY_MANAGER.getProperty("conviva.auth.phone.number");
    private final static String PHONE_PREFIX = PROPERTY_MANAGER.getProperty("conviva.auth.phone.prefix");

    public Configuration getConfiguration() {

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "v1/configuration"))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: {}", response.statusCode());
            log().info("Signal CLI REST API Response: {}", response.body());

            final JSONObject jsonResponse = new JSONObject(response.body());
            final Configuration configuration = new Configuration();

            if (jsonResponse.has("logging")) {
                final JSONObject loggingObject = jsonResponse.getJSONObject("logging");

                if (loggingObject.has("Level")) {
                    final String level = loggingObject.getString("Level");
                    final Logging logging = new Logging();
                    logging.Level = level;
                    configuration.Logging = logging;
                }
                return configuration;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<Group> getGroups() {

        final List<Group> groups = new ArrayList<>();

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "v1/groups/" + PHONE_PREFIX + PHONE_NUMBER))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: {}", response.statusCode());
            log().info("Signal CLI REST API Response: {}", response.body());

            final JSONArray jsonResponse = new JSONArray(response.body());

            for (int i = 0; i < jsonResponse.length(); i++) {
                final JSONObject groupObject = jsonResponse.getJSONObject(i);
                final String id = groupObject.getString("id");
                final String name = groupObject.getString("name");
                final String description = groupObject.getString("description");
                final String internalId = groupObject.getString("internal_id");

                final Group group = new Group();
                group.setId(id);
                group.setName(name);
                group.setDescription(description);
                group.setInternalId(internalId);

                groups.add(group);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return groups;
    }

    /**
     * Read latest messages from account using:
     * http://<API_URL>/v1/receive/<PHONE_NUMBER>
     *
     * @return
     */
    public List<Message> getLatestMessages() {

        final List<Message> messages = new ArrayList<>();

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "v1/receive/" + PHONE_PREFIX + PHONE_NUMBER))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: {}", response.statusCode());
            log().info("Signal CLI REST API Response: {}", response.body());

            final JSONArray jsonResponse = new JSONArray(response.body());

            for (int i = 0; i < jsonResponse.length(); i++) {
                final JSONObject messageObject = jsonResponse.getJSONObject(i);

                if (messageObject.has("envelope")) {
                    final JSONObject envelopeObject = messageObject.getJSONObject("envelope");
                    final Message message = new Message();
                    // extract message
                    if (envelopeObject.has("dataMessage")) {
                        final JSONObject dataMessageObject = envelopeObject.getJSONObject("dataMessage");

                        // fixme: this will filter all reactions.
                        if (dataMessageObject.has("message") && !dataMessageObject.isNull("message")) {
                            final String messageText = dataMessageObject.getString("message");
                            message.setMessage(messageText);
                        }

                        // received in group.
                        if (dataMessageObject.has("groupInfo") && !dataMessageObject.isNull("groupInfo")) {
                            final JSONObject groupInfoObject = dataMessageObject.getJSONObject("groupInfo");
                            final String groupId = groupInfoObject.getString("groupId");
                            final String groupName = groupInfoObject.getString("groupName");
                            final GroupInfo groupInfo = new GroupInfo(groupId, groupName);

                            message.setGroupInfo(groupInfo);
                        }

                        // quoted message detection.
                        if (dataMessageObject.has("quote") && !dataMessageObject.isNull("quote")) {
                            final JSONObject quoteObject = dataMessageObject.getJSONObject("quote");
                            final String quotedText = quoteObject.getString("text");
                            final String quotedAuthor = quoteObject.getString("author");

                            final QuotedMessage quotedMessage = new QuotedMessage(quotedAuthor, quotedText);
                            message.setQuote(quotedMessage);
                        }
                    }

                    // extract meta data one by one.
                    if (envelopeObject.has("sourceName")) {
                        final String sourceName = envelopeObject.getString("sourceName");
                        message.setSourceName(sourceName);
                    }

                    if (envelopeObject.has("timestamp")) {
                        final Long timestamp = envelopeObject.getLong("timestamp");
                        message.setTimestamp(timestamp);
                    }

                    // add to list.
                    messages.add(message);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    /**
     * curl -X POST -H "Content-Type: application/json" 'http://<URL>/v2/send' -d '{"message": "Test via Signal API!", "number": "<PHONE_NUMBER>", "recipients": [ "<RECIPIENT_PHONE>" ]}'
     */
    public void postSendMessage(String message, String recipient) {

        // curl -X POST -H "Content-Type: application/json" 'http://192.168.178.126:9999/v2/send' -d '{"message": "Test via Signal API!", "number": "+491628293597", "recipients": [ "+4915142324728" ]}'
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("message", message);
        jsonBody.put("number", PHONE_PREFIX + PHONE_NUMBER);
        final JSONArray recipientsArray = new JSONArray();
        recipientsArray.put(recipient);
        jsonBody.put("recipients", recipientsArray);

        final HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "v2/send"))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log().info("Response Code: {}", response.statusCode());
            log().info("Signal CLI REST API Response: {}", response.body());

            final JSONObject jsonResponse = new JSONObject(response.body());
            if (jsonResponse.has("timestamp")) {
                final String timestamp = jsonResponse.getString("timestamp");

                // convert timestamp to human readable date
                final Date date = new Date(Long.parseLong(timestamp));
                final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone(PROPERTY_MANAGER.getProperty("conviva.bot.timezone.target")));
                final String formatted = format.format(date);
                log().info("Message sent at timestamp: {}", formatted);
            } else {
                log().error("Signal CLI REST API Response did not contain timestamp.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
