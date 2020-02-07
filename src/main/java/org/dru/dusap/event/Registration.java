package org.dru.dusap.event;

import java.util.Objects;
import java.util.function.Consumer;

final class Registration<E> {
    private final Consumer<E> listener;
    private final EventDelegator delegator;

    Registration(final Consumer<E> listener, final EventDelegator delegator) {
        this.listener = listener;
        this.delegator = (delegator != null ? delegator : DirectEventDelegator.INSTANCE);
    }

    Consumer<E> getListener() {
        return listener;
    }

    EventDelegator getDelegator() {
        return delegator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration)) return false;
        final Registration<?> that = (Registration<?>) o;
        return listener.equals(that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listener);
    }
}
