package de.codefever.conviva.api.signal.websocket;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class SignalWebSocketClient implements Loggable, PropertyManagerProvider {

    private final static String PHONE_NUMBER = PROPERTY_MANAGER.getProperty("conviva.auth.phone.number");
    private final static String PHONE_PREFIX = PROPERTY_MANAGER.getProperty("conviva.auth.phone.prefix");
    private final static String WS_URL = "ws://192.168.178.126:9999/v1/receive/" + PHONE_PREFIX + PHONE_NUMBER;

    public SignalWebSocketClient() {

    }

    public void start() {

        final HttpClient client = HttpClient.newHttpClient();
        final WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new SignalWebSocketListener())
                .join();
    }
}
