package org.dru.dusap.event;

import java.util.Objects;

public class PropertyEvent {
    private Object source;
    private String name;
    private Object oldValue;
    private Object newValue;

    public PropertyEvent(final Object source, final String name, final Object oldValue, final Object newValue) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(name, "name");
        this.source = source;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public final Object getSource() {
        return source;
    }

    public final String getName() {
        return name;
    }

    public final Object getOldValue() {
        return oldValue;
    }

    public final <T> T getOldValue(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        return type.cast(getOldValue());
    }

    public final Object getNewValue() {
        return newValue;
    }

    public final <T> T getNewValue(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        return type.cast(getNewValue());
    }
}
