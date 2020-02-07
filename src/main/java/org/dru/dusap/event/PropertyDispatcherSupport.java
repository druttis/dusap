package org.dru.dusap.event;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PropertyDispatcherSupport implements PropertyDispatcher {
    private final EventDispatcher dispatcher;
    private final Map<String, RegistrationList<PropertyEvent>> registrationListMap;

    private final EventBus eventBus;

    private PropertyDispatcherSupport(final EventBus eventBus, final EventDispatcher dispatcher) {
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        registrationListMap = new ConcurrentHashMap<>();
    }

    public PropertyDispatcherSupport(final EventDispatcher dispatcher) {
        this(null, Objects.requireNonNull(dispatcher, "dispatcher"));

    }

    public PropertyDispatcherSupport(final EventBus eventBus) {
        this(Objects.requireNonNull(eventBus, "eventBus"), null);
    }

    @Override
    public final void addPropertyListener(final String name, final Consumer<PropertyEvent> listener,
                                          final EventDelegator delegator) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(listener, "listener");
        registrationListMap.computeIfAbsent(name, $ -> new RegistrationList<>())
                .addEventListener(listener, delegator);
    }

    @Override
    public final void addPropertyListener(final String name, final Consumer<PropertyEvent>listener) {
        addPropertyListener(name, listener, null);
    }

    @Override
    public final void removePropertyListener(final String name, final Consumer<PropertyEvent>listener) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(listener, "listener");
        registrationListMap.computeIfPresent(name, ($, registrationList) -> {
            registrationList.removeEventListener(listener);
            return (registrationList.hasListener() ? registrationList : null);
        });
    }

    @Override
    public final void firePropertyEvent(final PropertyEvent event) {
        Objects.requireNonNull(event, "event");
        final RegistrationList<PropertyEvent> registrationList = registrationListMap.get(event.getName());
        if (registrationList != null) {
            registrationList.delegateEvent(event);
        }
        if (dispatcher != null) {
            dispatcher.fireEvent(event);
        } else {
            eventBus.postEvent(event);
        }
    }

    @Override
    public void firePropertyEvent(final String name, final Object oldValue, final Object newValue) {
        firePropertyEvent(new PropertyEvent(this, name, oldValue, newValue));
    }
}
