package org.dru.dusap.event;

import java.util.List;
import java.util.function.Consumer;

public enum DirectEventDelegator implements EventDelegator {
    INSTANCE;

    @Override
    public <E> void delegateEventToListenerList(final E event, final List<Consumer<E>> listenerList) {
        listenerList.forEach(listener -> {
            try {
                listener.accept(event);
            } catch (final RuntimeException exc) {
                System.err.println("unhandled runtime-exception caught:");
                exc.printStackTrace();
            }
        });
    }
}
