package org.dru.dusap.event;

import java.util.Objects;
import java.util.function.Consumer;

public final class EventBusImpl implements EventBus {
    private final RegistrationList<Object> registrationList;

    EventBusImpl() {
        registrationList = new RegistrationList<>();
    }

    @Override
    public void addEventListener(final Consumer<Object> listener, final EventDelegator delegator) {
        Objects.requireNonNull(listener, "listener");
        registrationList.addEventListener(listener, delegator);
    }

    @Override
    public void addEventListener(final Consumer<Object> listener) {
        Objects.requireNonNull(listener, "listener");
        addEventListener(listener, null);
    }

    @Override
    public void removeEventListener(final Consumer<Object> listener) {
        Objects.requireNonNull(listener, "listener");
        registrationList.removeEventListener(listener);
    }

    @Override
    public void postEvent(final Object event) {
        Objects.requireNonNull(event, "event");
        registrationList.delegateEvent(event);
    }
}
