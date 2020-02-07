package org.dru.dusap.event;

import java.util.List;
import java.util.function.Consumer;

public interface EventDelegator {
    <E> void delegateEventToListenerList(E event, List<Consumer<E>> listeners);
}
