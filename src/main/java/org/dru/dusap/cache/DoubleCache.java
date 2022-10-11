package org.dru.dusap.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DoubleCache<K, V> extends AbstractCache<K, V> {
    private final Cache<K, V> primary;
    private final Cache<K, V> secondary;

    public DoubleCache(final Cache<K, V> primary, final Cache<K, V> secondary) {
        Objects.requireNonNull(primary, "primary");
        Objects.requireNonNull(secondary, "secondary");
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public int checksum() {
        return primary.checksum();
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        return primary.getAll(keys);
    }

    @Override
    public Map<K, V> peekAll(final Set<K> keys) {
        final Set<K> local = new HashSet<>(keys);
        final Map<K, V> result = primary.peekAll(local);
        local.removeAll(result.keySet());
        if (!local.isEmpty()) {
            final Map<K, V> fetched = secondary.peekAll(local);
            primary.putAll(fetched);
            result.putAll(fetched);
        }
        return result;
    }

    @Override
    public void putAll(final Map<K, V> map) {
        primary.putAll(map);
        secondary.putAll(map);
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
