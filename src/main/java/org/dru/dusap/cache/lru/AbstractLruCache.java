package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheUpdate;
import org.dru.dusap.cache.FetchingCache;
import org.dru.dusap.cache.CacheEntry;
import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toSet;

public abstract class AbstractLruCache<K extends Serializable, V extends Serializable> extends FetchingCache<K, V> {
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

    @Override
    protected Map<K, V> peekAllImpl() {
        return peekAllImpl(new HashSet<>(entries.keySet()));
    }

    @Override
    protected final Map<K, V> peekAllImpl(final Set<K> keys) {
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
    protected final void putAllImpl(final Map<K, V> map) {
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
    protected final void updateAllImpl(final Map<K, CacheUpdate<V>> map) {
        final Instant now = now();
        map.forEach((key, update) -> {
            if (!Objects.equals(update.getOldValue(), update.getNewValue())) {
                entries.compute(key, ($, entry) -> {
                    V value = (entry != null ? entry.getValue() : null);
                    if (Objects.equals(value, update.getOldValue())) {
                        entry = new CacheEntry<>(update.getNewValue(), now.plus(getTimeToLive()));
                    }
                    return entry;
                });
            }
        });
    }

    @Override
    protected final void removeAllImpl(final Set<K> keys) {
        entries.keySet().removeAll(keys);
    }

    @Override
    protected final void removeAllImpl(final Map<K, V> map) {
        map.forEach(entries::remove);
    }

    @Override
    protected final void retainAllImpl(final Set<K> keys) {
        entries.keySet().retainAll(keys);
    }

    @Override
    protected final void clearImpl() {
        entries.clear();
    }

    public final Duration getTimeToLive() {
        return timeToLive;
    }

    public final void cleanup() {
        write(this::cleanupImpl);
    }

    protected final Instant now() {
        return timeProvider.getCurrentTime();
    }

    private void cleanupImpl() {
        final Instant now = now();
        entries.values().removeIf(entry -> entry.hasExpired(now));
    }
}
