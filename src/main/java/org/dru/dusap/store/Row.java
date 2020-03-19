package org.dru.dusap.store;

public final class Row<V> {
    public static <V> Row<V> create(final V value, final long modified) {
        return new Row<>(value, modified);
    }

    public static <V> Row<V> create() {
        return new Row<>(null, 0L);
    }

    private final V value;
    private final long modified;

    private Row(final V value, final long modified) {
        this.value = value;
        this.modified = modified;
    }

    public V value() {
        return value;
    }

    public long modified() {
        return modified;
    }

    @Override
    public String toString() {
        return value + " (" + modified + ")";
    }
}
