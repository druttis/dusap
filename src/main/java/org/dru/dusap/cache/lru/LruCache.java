package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.AbstractCache;
import org.dru.dusap.cache.CacheEntry;
import org.dru.dusap.cache.CacheMissHandler;
import org.dru.dusap.concurrent.Guard;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class LruCache<K, V> extends AbstractCache<K, V> {
    private final Duration timeToLive;
    private final CacheMissHandler<K, V> missHandler;
    private final TimeProvider timeProvider;
    private final Map<K, CacheEntry<V>> entries;
    private final ReadWriteLock readWriteLock;
    private final Object monitor;
    private final Set<K> pendingKeys;

    LruCache(final Duration timeToLive, final CacheMissHandler<K, V> missHandler, final TimeProvider timeProvider) {
        this.timeToLive = timeToLive;
        this.missHandler = missHandler;
        this.timeProvider = timeProvider;
        entries = new HashMap<>();
        readWriteLock = new ReentrantReadWriteLock(true);
        monitor = new Object();
        pendingKeys = ConcurrentHashMap.newKeySet();
    }

    @Override
    public int checksum() {
        return Guard.lock(readWriteLock.readLock(), () -> entries.keySet().hashCode());
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        Objects.requireNonNull(keys, "keys");
        // We need a mutable copy of the keys.
        final Set<K> local = new HashSet<>(keys);
        final Instant now = timeProvider.getCurrentTime();
        // Get stored keys.
        final Map<K, V> result = peekAll(local, now);
        // Remove keys from local.
        local.removeAll(result.keySet());
        // Remaining keys in local are cache-misses.
        if (!local.isEmpty()) {
            // So determine which keys we need to get from the cache-miss-handler
            // and the remaining local keys are keys currently being requested
            // by another thread that we need to wait for.
            final Set<K> fetchKeys = new HashSet<>();
            synchronized (monitor) {
                final Iterator<K> it = local.iterator();
                while (it.hasNext()) {
                    final K key = it.next();
                    if (!pendingKeys.contains(key)) {
                        fetchKeys.add(key);
                        it.remove();
                    }
                }
            }
            // Fetch.
            if (!fetchKeys.isEmpty()) {
                synchronized (monitor) {
                    pendingKeys.addAll(fetchKeys);
                }
                try {
                    final Map<K, V> fetched = missHandler.fetchAll(fetchKeys);
                    putAll(fetched);
                    result.putAll(fetched);
                } finally {
                    synchronized (monitor) {
                        pendingKeys.removeAll(fetchKeys);
                        monitor.notifyAll();
                    }
                }
            }
            // Wait for key's fetched by another thread.
            if (!local.isEmpty()) {
                try {
                    synchronized (monitor) {
                        while (local.stream().anyMatch(pendingKeys::contains)) {
                            monitor.wait();
                        }
                    }
                } catch (final InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                result.putAll(peekAll(local, now));
            }
        }
        return result;
    }

    @Override
    public Map<K, V> peekAll(final Set<K> keys) {
        Objects.requireNonNull(keys, "keys");
        return peekAll(keys, timeProvider.getCurrentTime());
    }

    @Override
    public void putAll(final Map<K, V> map) {
        Objects.requireNonNull(map, "map");
        final Instant expires = timeProvider.getCurrentTime().plus(timeToLive);
        Guard.lock(readWriteLock.writeLock(), () -> map.forEach((key, value)
                -> entries.put(key, new CacheEntry<>(value, expires)))
        );
    }

    @Override
    public void removeAll(final Set<K> keys) {
        Objects.requireNonNull(keys, "keys");
        Guard.lock(readWriteLock.writeLock(), () -> entries.keySet().removeAll(keys));
    }

    @Override
    public void removeAll(final Map<K, V> map) {
        Objects.requireNonNull(map, "map");
        Guard.lock(readWriteLock.writeLock(), () -> map.forEach(entries::remove));
    }

    @Override
    public void retainAll(final Set<K> keys) {
        Objects.requireNonNull(keys, "keys");
        Guard.lock(readWriteLock.writeLock(), () -> entries.keySet().retainAll(keys));
    }

    @Override
    public void clear() {
        Guard.lock(readWriteLock.writeLock(), entries::clear);
    }

    private Map<K, V> peekAll(final Set<K> keys, final Instant now) {
        cleanup(now);
        return Guard.lock(readWriteLock.readLock(), () -> {
            final Map<K, V> result = new HashMap<>();
            for (final K key : keys) {
                final CacheEntry<V> entry = entries.computeIfPresent(key, ($, existing)
                        -> existing.hasExpired(now) ? null : existing);
                if (entry != null) {
                    result.put(key, entry.getValue());
                }
            }
            return result;
        });
    }

    private void cleanup(final Instant now) {
        Guard.lock(readWriteLock.writeLock(), () -> entries.values().removeIf(entry -> entry.hasExpired(now)));
    }

    @Override
    public String toString() {
        return "LruCache";
    }
}
