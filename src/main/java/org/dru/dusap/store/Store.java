package org.dru.dusap.store;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface Store<K, V> {
    Map<K, V> getAll(Set<K> keys);

    V get(K key);

    Map<K, V> computeAll(Set<K> keys, BiFunction<K, V, V> operator);

    Map<K, V> computeAll(Map<K, BiFunction<K, V, V>> operators);

    V compute(K key, BiFunction<K, V, V> operator);

    Map<K, V> updateAll(Set<K> keys, UnaryOperator<V> operator);

    Map<K, V> updateAll(Map<K, UnaryOperator<V>> operators);

    V update(K key, UnaryOperator<V> operator);

    void putAll(Map<K, V> entries);

    void put(K key, V value);

    Set<K> removeAll(Set<K> keys);

    boolean remove(K key);
}
