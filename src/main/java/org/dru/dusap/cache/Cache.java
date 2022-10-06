package org.dru.dusap.cache;

import java.util.Map;
import java.util.Set;

public interface Cache<K, V> {
    V get(K key);

    Map<K, V> getAll(Set<K> keys);

    V peek(K key);

    Map<K, V> peekAll(Set<K> keys);

    void put(K key, V value);

    void putAll(Map<K, V> map);

    void remove(K key);

    void removeAll(Set<K> keys);

    void remove(K key, V value);

    void removeAll(Map<K, V> map);

    void clear();
}
