package org.dru.dusap.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DoubleCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Cache<K, V> primary;
    private final Cache<K, V> secondary;

    public DoubleCache(final Cache<K, V> primary, final Cache<K, V> secondary) {
        Objects.requireNonNull(primary, "primary");
        Objects.requireNonNull(secondary, "secondary");
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public Map<K, V> peekAll() {
        return primary.peekAll();
    }

    @Override
    public Map<K, V> peekAll(final Set<K> keys) {
        return primary.peekAll(keys);
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        return primary.getAll(keys);
    }

    @Override
    public void putAll(final Map<K, V> map) {
        primary.putAll(map);
        secondary.putAll(map);
    }

    @Override
    public void updateAll(final Map<K, CacheUpdate<V>> map) {
        primary.updateAll(map);
        secondary.updateAll(map);
    }

    @Override
    public void removeAll(final Set<K> keys) {
        primary.removeAll(keys);
        secondary.removeAll(keys);
    }

    @Override
    public void removeAll(final Map<K, V> map) {
        primary.removeAll(map);
        secondary.removeAll(map);
    }

    @Override
    public void retainAll(final Set<K> keys) {
        primary.retainAll(keys);
        secondary.retainAll(keys);
    }

    @Override
    public void clear() {
        primary.clear();
        secondary.clear();
    }

    @Override
    public String toString() {
        return "DoubleCache{" +
                "primary=" + primary +
                ", secondary=" + secondary +
                '}';
    }
}
