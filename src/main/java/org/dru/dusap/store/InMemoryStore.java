package org.dru.dusap.store;

import org.dru.dusap.concurrent.Guard;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;

import static java.util.Collections.emptySet;

public final class InMemoryStore<K extends Serializable, V extends Serializable> implements Store<K, V> {
    private final Map<K, V> entries;
    private final Guard guard;

    public InMemoryStore() {
        entries = new HashMap<>();
        guard = new Guard(new ReentrantReadWriteLock(true));
    }

    @Override
    public Set<K> getKeys(final int limit, final int page) {
        if (limit < 0) {
            throw new IllegalArgumentException("Negative limit: " + limit);
        }
        if (page < 0) {
            throw new IllegalArgumentException("Negative page: " + page);
        }
        if (limit == 0) {
            return emptySet();
        }
        final int skip = limit * page;
        return guard.read(() -> {
            final Set<K> result = new HashSet<>();
            final Iterator<K> it = entries.keySet().iterator();
            for (int i = 0; it.hasNext() && i < skip; i++) {
                it.next();
            }
            for (int i = 0; it.hasNext() && i < limit; i++) {
                result.add(it.next());
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
