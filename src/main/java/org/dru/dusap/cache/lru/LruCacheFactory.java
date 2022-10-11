package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;

import java.time.Duration;

public interface LruCacheFactory {
    <K, V> SmartLruCache<K, V> getSmartLruCache(final Duration timeToLive, final CacheFetcher<K, V> missHandler);

    <K, V> NaiveLruCache<K, V> getNaiveLruCache(final Duration timeToLive, final CacheFetcher<K, V> missHandler);
}
