package org.dru.dusap.event;

import java.util.Set;

public interface EventPeer extends EventSource {
    Set<String> getReservedPropertyNames();

    boolean isReservedProperty(String name);

    Set<String> getClientPropertyNames();

    boolean hasClientProperty(String name);

    Object getClientProperty(String name);

    <T> T getClientProperty(String name, Class<? extends T> type);

    void putClientProperty(String name, Object value);
}
