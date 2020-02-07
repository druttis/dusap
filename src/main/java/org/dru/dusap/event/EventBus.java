package org.dru.dusap.event;

import java.util.function.Consumer;

public interface EventBus {
    void addEventListener(Consumer<Object> listener, EventDelegator delegator);

    void addEventListener(Consumer<Object> listener);

    void removeEventListener(Consumer<Object> listener);

    void postEvent(Object event);
}
