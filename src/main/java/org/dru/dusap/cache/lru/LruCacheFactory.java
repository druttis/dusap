package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheMissHandler;

import java.time.Duration;

public interface LruCacheFactory {
    <K, V> LruCache<K, V> getCache(final Duration timeToLive, final CacheMissHandler<K, V> missHandler);
}
