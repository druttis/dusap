package org.dru.dusap.database.store;

import java.util.function.UnaryOperator;

public interface DbStore<K, V> {
    V get(K key);

    V update(K key, UnaryOperator<V> updater);

    void delete(K key);
}
