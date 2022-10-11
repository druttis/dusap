package org.dru.dusap.cache;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

public abstract class AbstractCache<K, V> implements Cache<K, V> {
    @Override
    public final V get(final K key) {
        return getAll(singleton(key)).get(key);
    }

    @Override
    public final V peek(final K key) {
        return peekAll(singleton(key)).get(key);
    }

    @Override
    public final void put(final K key, final V value) {
        putAll(singletonMap(key, value));
    }

    @Override
    public final void remove(final K key) {
        removeAll(singleton(key));
    }

    @Override
    public final void remove(final K key, final V value) {
        removeAll(singletonMap(key, value));
    }
}
