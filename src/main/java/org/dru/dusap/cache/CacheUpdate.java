package org.dru.dusap.cache;

public final class CacheUpdate<V> {
    private final V oldValue;
    private final V newValue;

    public CacheUpdate(final V oldValue, final V newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public V getOldValue() {
        return oldValue;
    }

    public V getNewValue() {
        return newValue;
    }
}
