package org.dru.dusap.event;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventDispatcherSupport implements EventDispatcher {
    private final Map<Class<?>, RegistrationList<?>> registrationListMap;

    private final EventBus eventBus;

    public EventDispatcherSupport(final EventBus eventBus) {
        Objects.requireNonNull(eventBus, "eventBus");
        this.eventBus = eventBus;
        registrationListMap = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> void addEventListener(final Class<E> type, final Consumer<E> listener,
                                           final EventDelegator delegator) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(listener, "listener");
        ((RegistrationList<E>) registrationListMap.computeIfAbsent(type, $ -> new RegistrationList<>()))
                .addEventListener(listener, delegator);
    }

    @Override
    public final <E> void addEventListener(final Class<E> type, final Consumer<E> listener) {
        addEventListener(type, listener, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> void removeEventListener(final Class<E> type, final Consumer<E> listener) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(listener, "listener");
        registrationListMap.computeIfPresent(type, ($, registrationList) -> {
            ((RegistrationList<E>) registrationList).removeEventListener(listener);
            return (registrationList.hasListener() ? registrationList : null);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E> void fireEvent(final E event) {
        Objects.requireNonNull(event, "event");
        final RegistrationList<E> registrationList = (RegistrationList<E>) registrationListMap.get(event.getClass());
        if (registrationList != null) {
            registrationList.delegateEvent(event);
        }
        eventBus.postEvent(event);
    }

    protected final EventBus getEventBus() {
        return eventBus;
    }
}
