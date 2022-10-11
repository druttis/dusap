package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

final class NaiveLruCache<K, V> extends AbstractLruCache<K, V> {
    NaiveLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive, final TimeProvider timeProvider) {
        super(fetcher, timeToLive, timeProvider);
    }

    @Override
    protected Map<K, V> getAllInternal(final Set<K> keys) {
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
