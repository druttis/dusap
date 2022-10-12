package org.dru.dusap.store;

import org.dru.dusap.concurrent.Guard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;

import static java.util.Collections.emptyMap;

public final class InMemoryStore<K, V> implements Store<K, V> {
    private final Map<K, V> entries;
    private final Guard guard;

    public InMemoryStore() {
        entries = new HashMap<>();
        guard = new Guard(new ReentrantReadWriteLock(true));
    }

    @Override
    public Map<K, V> getAll(final int limit, final int page) {
        if (limit < 0) {
            throw new IllegalArgumentException("Negative limit: " + limit);
        }
        if (page < 0) {
            throw new IllegalArgumentException("Negative page: " + page);
        }
        if (limit == 0) {
            return emptyMap();
        }
        final int skip = limit * page;
        return guard.read(() -> {
            final Map<K, V> result = new HashMap<>();
            final Iterator<Map.Entry<K, V>> it = entries.entrySet().iterator();
            for (int i = 0; it.hasNext() && i < skip; i++) {
                it.next();
            }
            for (int i = 0; it.hasNext() && i < limit; i++) {
                final Map.Entry<K, V> entry = it.next();
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        });
    }

    @Override
    public V get(final K key) {
        return guard.read(() -> entries.get(key));
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        final Map<K, V> result = new HashMap<>();
        guard.write(() -> {
            for (final K key : keys) {
                final V value = entries.get(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
        });
        return result;
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operation) {
        return guard.update(() -> entries.compute(key, ($, entry) -> operation.apply(entry)));
    }
}
