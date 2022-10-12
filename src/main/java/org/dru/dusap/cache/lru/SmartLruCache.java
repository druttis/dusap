package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.util.*;

final class SmartLruCache<K, V> extends AbstractLruCache<K, V> {
    private final Object monitor;
    private final Set<K> pendingKeys;

    SmartLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive, final TimeProvider timeProvider) {
        super(fetcher, timeToLive, timeProvider);
        monitor = new Object();
        pendingKeys = new HashSet<>();
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        // Get stored keys.
        final Map<K, V> result = peekAll(keys);
        final Set<K> missingKeys = missingKeys(result.keySet(), keys);
        if (!missingKeys.isEmpty()) {
            final Set<K> keysToFetch;
            synchronized (monitor) {
                keysToFetch = missingKeys(pendingKeys, missingKeys);
            }
            if (!keysToFetch.isEmpty()) {
                final Map<K, V> fetched;
                try {
                    synchronized (monitor) {
                        pendingKeys.addAll(keysToFetch);
                    }
                    fetched = fetchAll(keysToFetch);
                } finally {
                    synchronized (monitor) {
                        pendingKeys.removeAll(keysToFetch);
                        monitor.notifyAll();
                    }
                }
                result.putAll(fetched);
            }
            final Set<K> keysToAwait = missingKeys(missingKeys, keysToFetch);
            if (!keysToAwait.isEmpty()) {
                try {
                    synchronized (monitor) {
                        while (missingKeys.stream().anyMatch(pendingKeys::contains)) {
                            monitor.wait();
                        }
                    }
                } catch (final InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                result.putAll(peekAll(keysToAwait));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "SmartLruCache";
    }
}
