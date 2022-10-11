package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.FetchingCache;
import org.dru.dusap.cache.CacheEntry;
import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.concurrent.Guard;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractLruCache<K, V> extends FetchingCache<K, V> {
    protected static <K> Set<K> missingKeys(final Set<K> source, final Set<K> keys) {
        return keys.stream().filter(key -> !source.contains(key)).collect(toSet());
    }

    private final Duration timeToLive;
    private final TimeProvider timeProvider;
    private final Map<K, CacheEntry<V>> entries;

    public AbstractLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive,
                            final TimeProvider timeProvider) {
        super(fetcher);
        Objects.requireNonNull(timeToLive, "timeToLive");
        if (timeToLive.isNegative()) {
            throw new IllegalArgumentException("Negative timeToLive: " + timeToLive);
        }
        Objects.requireNonNull(timeProvider, "timeProvider");
        this.timeToLive = timeToLive;
        this.timeProvider = timeProvider;
        entries = new HashMap<>();
    }

    public final Duration getTimeToLive() {
        return timeToLive;
    }

    public final void cleanup() {
        Guard.lock(writeLock(), this::cleanupInternal);
    }

    @Override
    protected final int checksumInternal() {
        return entries.keySet().hashCode();
    }

    @Override
    protected final Map<K, V> peekAllInternal(final Set<K> keys) {
        final Instant now = now();
        final Map<K, V> result = new HashMap<>();
        for (final K key : keys) {
            final CacheEntry<V> entry = entries.computeIfPresent(key, ($, existing)
                    -> existing.hasExpired(now) ? null : existing);
            if (entry != null) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    @Override
    protected final void putAllInternal(final Map<K, V> map) {
        final Instant now = now();
        map.forEach((key, value) -> {
            if (value != null) {
                entries.put(key, new CacheEntry<>(value, now.plus(getTimeToLive())));
            } else {
                entries.remove(key);
            }
        });
    }

    @Override
    protected void removeAllInternal(final Set<K> keys) {
        entries.keySet().removeAll(keys);
    }

    @Override
    protected void removeAllInternal(final Map<K, V> map) {
        map.forEach(entries::remove);
    }

    @Override
    protected void retainAllInternal(final Set<K> keys) {
        entries.keySet().retainAll(keys);
    }

    @Override
    protected void clearInternal() {
        entries.clear();
    }

    protected final Instant now() {
        return timeProvider.getCurrentTime();
    }

    private void cleanupInternal() {
        final Instant now = now();
        entries.values().removeIf(entry -> entry.hasExpired(now));
    }
}
