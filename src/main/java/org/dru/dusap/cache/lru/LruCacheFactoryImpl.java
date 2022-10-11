package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;

public final class LruCacheFactoryImpl implements LruCacheFactory {
    private final TimeProvider timeProvider;

    public LruCacheFactoryImpl(final TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public <K, V> SmartLruCache<K, V> getSmartLruCache(final Duration timeToLive,
                                                       final CacheFetcher<K, V> missHandler) {
        return new SmartLruCache<>(missHandler, timeToLive, timeProvider);
    }

    @Override
    public <K, V> NaiveLruCache<K, V> getNaiveLruCache(final Duration timeToLive,
                                                       final CacheFetcher<K, V> missHandler) {
        return new NaiveLruCache<>(missHandler, timeToLive, timeProvider);
    }
}
