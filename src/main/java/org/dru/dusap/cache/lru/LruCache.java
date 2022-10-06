package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.AbstractCache;
import org.dru.dusap.cache.CacheMissHandler;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

final class LruCache<K, V> extends AbstractCache<K, V> {
    private final Duration timeToLive;
    private final CacheMissHandler<K, V> missHandler;
    private final TimeProvider timeProvider;
    private final Map<K, LruEntry<V>> entries;

    LruCache(final Duration timeToLive, final CacheMissHandler<K, V> missHandler, final TimeProvider timeProvider) {
        this.timeToLive = timeToLive;
        this.missHandler = missHandler;
        this.timeProvider = timeProvider;
        entries = new HashMap<>();
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        final Map<K, V> result = peekAll(keys);
        final Set<K> missingKeys = keys.stream().filter(key -> !entries.containsKey(key)).collect(toSet());
        if (!missingKeys.isEmpty()) {
            final Map<K, V> fetched = missHandler.fetchAll(missingKeys);
            putAll(fetched);
            result.putAll(fetched);
        }
        return result;
    }

    @Override
    public Map<K, V> peekAll(final Set<K> keys) {
        final Map<K, V> result = new HashMap<>();
        final Instant now = timeProvider.getCurrentTime();
        for (final K key : keys) {
            final LruEntry<V> entry = entries.computeIfPresent(key, ($, existing)
                    -> existing.hasExpired(now) ? null : existing);
            if (entry != null) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void putAll(final Map<K, V> map) {
        final Instant expires = timeProvider.getCurrentTime().plus(timeToLive);
        map.forEach((key, value) -> entries.put(key, new LruEntry<>(value, expires)));
    }

    @Override
    public void removeAll(final Set<K> keys) {
        entries.keySet().removeAll(keys);
    }

    @Override
    public void removeAll(final Map<K, V> map) {
        map.forEach(entries::remove);
    }

    @Override
    public void clear() {
        entries.clear();
    }
}
