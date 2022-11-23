package org.dru.dusap.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class FetchingCache<K extends Serializable, V extends Serializable> extends BlockingCache<K, V> {
    private final CacheFetcher<K, V> fetcher;

    protected FetchingCache(final CacheFetcher<K, V> fetcher) {
        Objects.requireNonNull(fetcher, "fetcher");
        this.fetcher = fetcher;
    }

    protected final Map<K, V> fetchAll(final Set<K> keys) {
        final Map<K, V> result = fetcher.fetchAll(keys);
        putAll(result);
        return result;
    }
}
