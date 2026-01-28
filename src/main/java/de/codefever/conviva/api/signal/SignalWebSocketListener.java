package de.codefever.conviva.api.signal;

import de.codefever.conviva.model.signal.GroupInfo;
import de.codefever.conviva.model.signal.Message;
import de.codefever.conviva.model.signal.QuotedMessage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.json.JSONObject;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class SignalWebSocketListener implements Loggable, WebSocket.Listener {

    @Override
    public void onOpen(WebSocket webSocket) {
        log().info("WebSocket connection opened.");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        log().info("WebSocket message received: {}", data);

        try {
            final JSONObject messageObject = new JSONObject(data.toString());

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
                // forward to event bus
                log().info("Adding message to EventBus: {}", message);
                EventBus.getInstance().publish(message);
            }
        }
        catch (Exception e) {
            log().error("Error processing WebSocket message: {}", e.getMessage());
        }
        webSocket.request(1);
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        log().info("WebSocket connection closed: {}", reason);
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }
}
