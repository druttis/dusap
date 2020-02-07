package org.dru.dusap.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

final class RegistrationList<E> {
    private final List<Registration<E>> registrationList;

    RegistrationList() {
        this.registrationList = new CopyOnWriteArrayList<>();
    }

    boolean hasListener() {
        return !registrationList.isEmpty();
    }

    void addEventListener(final Consumer<E> listener, final EventDelegator delegator) {
        registrationList.add(new Registration<>(listener, delegator));
    }

    void removeEventListener(final Consumer<E> listener) {
        registrationList.remove(new Registration<>(listener, null));
    }

    void delegateEvent(final E event) {
        final Map<EventDelegator, List<Consumer<E>>> listenerListMap = new HashMap<>();
        for (final Registration<E> registration : registrationList) {
            listenerListMap.computeIfAbsent(registration.getDelegator(), $ -> new ArrayList<>())
                    .add(registration.getListener());
        }
        listenerListMap.forEach((delegator, listenerList) ->
                delegator.delegateEventToListenerList(event, listenerList));
    }
}
