package de.codefever.conviva.api.signal;

import de.codefever.conviva.model.signal.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {

    private static final EventBus INSTANCE = new EventBus();
    private final List<Consumer<Message>> listeners = new CopyOnWriteArrayList<>();

    public static EventBus getInstance() {
        return EventBus.INSTANCE;
    }

    public void subscribe(Consumer<Message> listener) {
        listeners.add(listener);
    }

    public void publish(final Message event) {
        // convert Object to message
        for (Consumer<Message> listener : listeners) {
            listener.accept(event);
        }
    }
}
