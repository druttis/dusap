package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;

import java.time.Duration;

public interface LruCacheFactory {
    <K, V> SmartLruCache<K, V> getSmartLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive);

    <K, V> NaiveLruCache<K, V> getNaiveLruCache(final CacheFetcher<K, V> fetcher, final Duration timeToLive);
}
