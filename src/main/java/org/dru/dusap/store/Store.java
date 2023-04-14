package org.dru.dusap.store;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface Store<K extends Serializable, V extends Serializable> {
    V get(K key);

    Map<K, V> getAll(Set<K> keys);

    V update(K key, StoreOperator<K, V> operator);

    default void remove(K key) {
        update(key, (k, v) -> null);
    }
}
