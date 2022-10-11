package org.dru.dusap.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

public interface CacheFetcher<K, V> {
    static <K, V> CacheFetcher<K, V> cache(final Cache<K, V> cache) {
        Objects.requireNonNull(cache, "cache");
        return new CacheFetcher<K, V>() {
            @Override
            public Map<K, V> fetchAll(final Set<K> keys) {
                return cache.getAll(keys);
            }
        };
    }

    default V fetch(K key) {
        return fetchAll(singleton(key)).get(key);
    }

    default Map<K, V> fetchAll(Set<K> keys) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), this::fetch));
    }
}
