package org.dru.dusap.store;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface Store<K extends Serializable, V extends Serializable> {
    Set<K> getKeys(int limit, int page);

    V get(K key);

    Map<K, V> getAll(Set<K> keys);

    V update(K key, UnaryOperator<V> operation);

    default void remove(K key) {
        update(key, value -> null);
    }
}
