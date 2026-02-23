package de.codefever.conviva.tests;

import de.codefever.conviva.AbstractTest;
import de.codefever.conviva.api.signal.event.EventBus;
import de.codefever.conviva.api.signal.rest.SignalCliRestApiClient;
import de.codefever.conviva.api.signal.websocket.SignalWebSocketClient;
import de.codefever.conviva.model.signal.Configuration;
import de.codefever.conviva.model.signal.SignalMessage;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.annotations.Test;

import java.util.List;

public class SignalBotTest extends AbstractTest {

    @Test
    public void testT00_GetConfiguration() {

        final SignalCliRestApiClient signalCliRestApiClient = new SignalCliRestApiClient();
        final Configuration configuration = signalCliRestApiClient.getConfiguration();
        ASSERT.assertEquals(configuration.Logging.Level, "info");
    }

    @Test
    public void testT01_GetLatestMessages() {

        final SignalCliRestApiClient signalCliRestApiClient = new SignalCliRestApiClient();
        final List<SignalMessage> messages = signalCliRestApiClient.getLatestMessages();
        ASSERT.assertNotNull(messages);
    }

    @Test
    public void testT02_PostSendMessage() {

        final SignalCliRestApiClient signalCliRestApiClient = new SignalCliRestApiClient();
        signalCliRestApiClient.postSendMessage("Test from Automated Test", "+4915142324728");
    }

    @Test
    public void testT04_GetLatestMessagesWebSocket() {
        final SignalWebSocketClient signalWebSocketClient = new SignalWebSocketClient();
        signalWebSocketClient.start();

        int timeoutInMs = 2 * 60 * 1000;

        EventBus.getInstance().subscribe(msg -> {
            log().info("Received message via WebSocket: {}", msg.toString());
        });

        while (timeoutInMs > 0) {
            TimerUtils.sleep(1000, "Waiting for messages...");
            timeoutInMs -= 1000;
        }
    }
}
