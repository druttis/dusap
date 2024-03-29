package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;
import org.dru.dusap.time.TimeProvider;

import java.io.Serializable;
import java.time.Duration;

public final class LruCacheFactoryImpl implements LruCacheFactory {
    private final TimeProvider timeProvider;

    public LruCacheFactoryImpl(final TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public <K extends Serializable, V extends Serializable> SmartLruCache<K, V> getSmartLruCache(
            final CacheFetcher<K, V> fetcher, final Duration timeToLive) {
        return new SmartLruCache<>(fetcher, timeToLive, timeProvider);
    }

    @Override
    public <K extends Serializable, V extends Serializable> NaiveLruCache<K, V> getNaiveLruCache(
            final CacheFetcher<K, V> fetcher, final Duration timeToLive) {
        return new NaiveLruCache<>(fetcher, timeToLive, timeProvider);
    }
}
