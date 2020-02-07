package org.dru.dusap.event;

import java.util.function.Consumer;

public interface EventDispatcher {
    <E> void addEventListener(Class<E> type, Consumer<E> listener, EventDelegator delegator);

    <E> void addEventListener(Class<E> type, Consumer<E> listener);

    <E> void removeEventListener(Class<E> type, Consumer<E> listener);

    <E> void fireEvent(E event);
}
