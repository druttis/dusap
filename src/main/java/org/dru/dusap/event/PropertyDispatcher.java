package org.dru.dusap.event;

import java.util.function.Consumer;

public interface PropertyDispatcher {
    void addPropertyListener(String name, Consumer<PropertyEvent> listener, EventDelegator delegator);

    void addPropertyListener(String name, Consumer<PropertyEvent>listener);

    void removePropertyListener(String name, Consumer<PropertyEvent>listener);

    void firePropertyEvent(PropertyEvent event);

    void firePropertyEvent(String name, Object oldValue, Object newValue);
}
