package org.dru.dusap.store;

import java.util.Map;
import java.util.Set;

public interface Bucket<K, V> {
    int num();

    Map<K, Row<V>> select(Set<K> keys, boolean lock);

    void begin();

    void upsert(K key, V value, long modified);

    void update(K key, V value, long modified);

    void delete(K key, long modified);

    void commit();

    void rollback();
}
