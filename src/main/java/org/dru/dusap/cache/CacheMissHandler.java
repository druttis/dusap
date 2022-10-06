package org.dru.dusap.cache;

import java.util.Collection;
import java.util.Map;

import static java.util.Collections.singleton;

public interface CacheMissHandler<K, V> {
    default V fetch(K key) {
        return fetchAll(singleton(key)).get(key);
    }

    Map<K, V> fetchAll(Collection<K> keys);
}
