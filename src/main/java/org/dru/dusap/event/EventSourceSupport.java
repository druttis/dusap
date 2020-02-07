package org.dru.dusap.event;

import java.util.function.Consumer;

public class EventSourceSupport extends EventDispatcherSupport implements PropertyDispatcher {
    private final PropertyDispatcherSupport support;

    public EventSourceSupport(final EventBus eventBus) {
        super(eventBus);
        support = new PropertyDispatcherSupport(this);
    }

    @Override
    public final void addPropertyListener(final String name, final Consumer<PropertyEvent> listener,
                                          final EventDelegator delegator) {
        support.addPropertyListener(name, listener, delegator);
    }

    @Override
    public final void addPropertyListener(final String name, final Consumer<PropertyEvent> listener) {
        support.addPropertyListener(name, listener);
    }

    @Override
    public final void removePropertyListener(final String name, final Consumer<PropertyEvent> listener) {
        support.removePropertyListener(name, listener);
    }

    @Override
    public final void firePropertyEvent(final PropertyEvent event) {
        support.firePropertyEvent(event);
    }

    @Override
    public final void firePropertyEvent(final String name, final Object oldValue, final Object newValue) {
        support.firePropertyEvent(name, oldValue, newValue);
    }


}
