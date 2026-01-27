package de.codefever.conviva.tests;

import de.codefever.conviva.AbstractTest;
import de.codefever.conviva.api.signal.SignalCliRestApiClient;
import de.codefever.conviva.model.signal.Configuration;
import de.codefever.conviva.model.signal.Message;
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
        final List<Message> messages = signalCliRestApiClient.getLatestMessages();
        ASSERT.assertNotNull(messages);
    }

    @Test
    public void testT02_PostSendMessage() {

        final SignalCliRestApiClient signalCliRestApiClient = new SignalCliRestApiClient();
        signalCliRestApiClient.postSendMessage("Test from Automated Test", "+4915142324728");

    }
}
