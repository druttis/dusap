package org.dru.dusap.cache.lru;

import org.dru.dusap.cache.CacheFetcher;

import java.io.Serializable;
import java.time.Duration;

public interface LruCacheFactory {
    <K extends Serializable, V extends Serializable> SmartLruCache<K, V> getSmartLruCache(
            final CacheFetcher<K, V> fetcher, final Duration timeToLive);

    <K extends Serializable, V extends Serializable> NaiveLruCache<K, V> getNaiveLruCache(
            final CacheFetcher<K, V> fetcher, final Duration timeToLive);
}
