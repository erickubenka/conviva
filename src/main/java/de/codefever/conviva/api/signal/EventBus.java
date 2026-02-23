package de.codefever.conviva.api.signal;

import de.codefever.conviva.model.signal.SignalMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {

    private static final EventBus INSTANCE = new EventBus();
    private final List<Consumer<SignalMessage>> listeners = new CopyOnWriteArrayList<>();

    public static EventBus getInstance() {
        return EventBus.INSTANCE;
    }

    public void subscribe(Consumer<SignalMessage> listener) {
        listeners.add(listener);
    }

    public void publish(final SignalMessage event) {
        // convert Object to message
        for (Consumer<SignalMessage> listener : listeners) {
            listener.accept(event);
        }
    }
}
