package org.dru.dusap.store;

import org.dru.dusap.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class DynamicStore<K, V> implements Store<K, V> {
    private final Store<K, V> store;

    public DynamicStore(final List<Bucket<K, V>> buckets) {
        CollectionUtils.requireNonNull(buckets, "buckets", "bucket");
        if (buckets.isEmpty()) {
            throw new IllegalArgumentException("no buckets specified");
        } else if (buckets.size() == 1) {
            store = new SimpleStore<>(buckets.get(0));
        } else {
            store = new DistributedStore<>(buckets);
        }
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        return store.getAll(keys);
    }

    @Override
    public V get(final K key) {
        Objects.requireNonNull(key, "key");
        return store.get(key);
    }

    @Override
    public Map<K, V> computeAll(final Set<K> keys, final BiFunction<K, V, V> operator) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        Objects.requireNonNull(operator, "operator");
        return store.computeAll(keys, operator);
    }

    @Override
    public Map<K, V> computeAll(final Map<K, BiFunction<K, V, V>> operators) {
        Objects.requireNonNull(operators, "operators");
        CollectionUtils.requireNonNull(operators.keySet(), "operators.keySet", "key");
        CollectionUtils.requireNonNull(operators.values(), "operators.values", "operator");
        return store.computeAll(operators);
    }

    @Override
    public V compute(final K key, final BiFunction<K, V, V> operator) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(operator, "operator");
        return store.compute(key, operator);
    }

    @Override
    public Map<K, V> updateAll(final Set<K> keys, final UnaryOperator<V> operator) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        Objects.requireNonNull(operator, "operator");
        return store.updateAll(keys, operator);
    }

    @Override
    public Map<K, V> updateAll(final Map<K, UnaryOperator<V>> operators) {
        Objects.requireNonNull(operators, "operators");
        CollectionUtils.requireNonNull(operators.keySet(), "operators.keySet", "key");
        CollectionUtils.requireNonNull(operators.values(), "operators.values", "operator");
        return store.updateAll(operators);
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(operator, "operator");
        return store.update(key, operator);
    }

    @Override
    public void putAll(final Map<K, V> entries) {
        Objects.requireNonNull(entries, "entries");
        CollectionUtils.requireNonNull(entries.keySet(), "entries.keySet", "key");
        store.putAll(entries);
    }

    @Override
    public void put(final K key, final V value) {
        Objects.requireNonNull(key, "key");
        store.put(key, value);
    }

    @Override
    public Set<K> removeAll(final Set<K> keys) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        return store.removeAll(keys);
    }

    @Override
    public boolean remove(final K key) {
        Objects.requireNonNull(key, "key");
        return store.remove(key);
    }
}
