package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

final class NaiveLruCache<K extends Serializable, V extends Serializable> extends AbstractLruCache<K, V> {
    NaiveLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive, final TimeProvider timeProvider) {
        super(fetcher, timeToLive, timeProvider);
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        final Map<K, V> result = peekAll(keys);
        final Set<K> missingKeys = missingKeys(result.keySet(), keys);
        if (!missingKeys.isEmpty()) {
            final Map<K, V> fetched = fetchAll(missingKeys);
            result.putAll(fetched);
        }
        return result;
    }

    @Override
    public String toString() {
        return "NaiveLruCache";
    }
}
