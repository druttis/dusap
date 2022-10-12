package org.dru.dusap.cache;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

public interface Cache<K, V> {
    Map<K, V> peekAll();

    Map<K, V> peekAll(Set<K> keys);

    default V peek(K key) {
        return peekAll(singleton(key)).get(key);
    }

    Map<K, V> getAll(Set<K> keys);

    default V get(K key) {
        return getAll(singleton(key)).get(key);
    }

    void putAll(Map<K, V> map);

    default void put(K key, V value) {
        putAll(singletonMap(key, value));
    }

    void updateAll(Map<K, CacheUpdate<V>> map);

    default void update(K key, CacheUpdate<V> update) {
        updateAll(singletonMap(key, update));
    }

    default void update(K key, V oldValue, V newValue) {
        update(key, new CacheUpdate<>(oldValue, newValue));
    }

    void removeAll(Set<K> keys);

    default void remove(K key) {
        removeAll(singleton(key));
    }

    void removeAll(Map<K, V> map);

    default void remove(K key, V value) {
        removeAll(singletonMap(key, value));
    }

    void retainAll(Set<K> keys);

    void clear();
}
