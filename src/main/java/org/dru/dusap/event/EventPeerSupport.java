package org.dru.dusap.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventPeerSupport extends EventSourceSupport implements EventPeer {
    private final Set<String> reservedPropertyNameSet;
    private final Map<String, Object> clientPropertyMap;

    public EventPeerSupport(final EventBus eventBus) {
        super(eventBus);
        reservedPropertyNameSet = ConcurrentHashMap.newKeySet();
        clientPropertyMap = new ConcurrentHashMap<>();
    }

    @Override
    public final Set<String> getReservedPropertyNames() {
        return new HashSet<>(reservedPropertyNameSet);
    }

    @Override
    public final boolean isReservedProperty(final String name) {
        Objects.requireNonNull(name, "name");
        return reservedPropertyNameSet.contains(name);
    }

    @Override
    public final Set<String> getClientPropertyNames() {
        return new HashSet<>(clientPropertyMap.keySet());
    }

    @Override
    public final boolean hasClientProperty(final String name) {
        Objects.requireNonNull(name, "name");
        return clientPropertyMap.containsKey(name);
    }

    @Override
    public final Object getClientProperty(final String name) {
        checkReservedProperty(name);
        return clientPropertyMap.get(name);
    }

    @Override
    public final <T> T getClientProperty(final String name, final Class<? extends T> type) {
        Objects.requireNonNull(type, "type");
        return type.cast(getClientProperty(name));
    }

    @Override
    public final void putClientProperty(final String name, final Object value) {
        checkReservedProperty(name);
        final Object old;
        if (value != null) {
            old = clientPropertyMap.put(name, value);
        } else {
            old = clientPropertyMap.remove(name);
        }
        if ((value == null && old != null) || (value != null && !value.equals(old))) {
            firePropertyEvent(name, old, value);
        }
    }

    protected final void reserveProperty(final String name) {
        if (!reservedPropertyNameSet.add(name)) {
            throw new IllegalArgumentException("property already reserved: " + name);
        }
    }

    private void checkReservedProperty(final String name) {
        if (isReservedProperty(name)) {
            throw new IllegalArgumentException("property is reserved: " + name);
        }
    }
}
