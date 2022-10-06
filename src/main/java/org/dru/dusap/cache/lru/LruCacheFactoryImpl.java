package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheMissHandler;
import org.dru.dusap.time.TimeProvider;

import java.time.Duration;
import java.util.Objects;

public final class LruCacheFactoryImpl implements LruCacheFactory {
    private final TimeProvider timeProvider;

    public LruCacheFactoryImpl(final TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public <K, V> LruCache<K, V> getCache(final Duration timeToLive, final CacheMissHandler<K, V> missHandler) {
        Objects.requireNonNull(timeToLive, "timeToLive");
        if (timeToLive.isNegative()) {
            throw new IllegalArgumentException("Negative timeToLive: " + timeToLive);
        }
        Objects.requireNonNull(missHandler, "missHandler");
        return new LruCache<>(timeToLive, missHandler, timeProvider);
    }
}
