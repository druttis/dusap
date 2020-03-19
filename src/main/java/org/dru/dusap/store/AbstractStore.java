package org.dru.dusap.store;

import org.dru.dusap.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class AbstractStore<K, V> implements Store<K, V> {
    @Override
    public final Map<K, V> getAll(final Set<K> keys) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        return getAllImpl(keys);
    }

    @Override
    public final V get(final K key) {
        Objects.requireNonNull(key, "key");
        return getAllImpl(Collections.singleton(key)).get(key);
    }

    @Override
    public final Map<K, V> computeAll(final Set<K> keys, final BiFunction<K, V, V> operator) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        Objects.requireNonNull(operator, "operator");
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        return computeAllImpl(keys, operator);
    }

    @Override
    public final Map<K, V> computeAll(final Map<K, BiFunction<K, V, V>> operators) {
        Objects.requireNonNull(operators, "operators");
        CollectionUtils.requireNonNull(operators.keySet(), "operators.keySet", "key");
        CollectionUtils.requireNonNull(operators.values(), "operators.values", "operator");
        if (operators.isEmpty()) {
            return Collections.emptyMap();
        }
        return computeAllImpl(operators.keySet(), (key, value) -> operators.get(key).apply(key, value));
    }

    @Override
    public final V compute(final K key, final BiFunction<K, V, V> operator) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(operator, "operator");
        return computeAllImpl(Collections.singleton(key), operator).get(key);
    }

    @Override
    public final Map<K, V> updateAll(final Set<K> keys, final UnaryOperator<V> operator) {
        Objects.requireNonNull(keys, "keys");
        Objects.requireNonNull(operator, "operator");
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        return computeAllImpl(keys, (key, value) -> operator.apply(value));
    }

    @Override
    public Map<K, V> updateAll(final Map<K, UnaryOperator<V>> operators) {
        Objects.requireNonNull(operators, "operators");
        CollectionUtils.requireNonNull(operators.keySet(), "operators.keySet", "key");
        CollectionUtils.requireNonNull(operators.values(), "operators.values", "operator");
        if (operators.isEmpty()) {
            return Collections.emptyMap();
        }
        return computeAllImpl(operators.keySet(), (key, value) -> operators.get(key).apply(value));
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(operator, "operator");
        return computeAllImpl(Collections.singleton(key), ($, value) -> operator.apply(value)).get(key);
    }

    @Override
    public final void putAll(final Map<K, V> entries) {
        Objects.requireNonNull(entries, "entries");
        CollectionUtils.requireNonNull(entries.keySet(), "entries.keySet", "key");
        if (entries.isEmpty()) {
            return;
        }
        computeAll(entries.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (e) -> (k, v) -> e.getValue()))
        );
    }

    @Override
    public final void put(final K key, final V value) {
        Objects.requireNonNull(key, "key");
        putAll(Collections.singletonMap(key, value));
    }

    @Override
    public final Set<K> removeAll(final Set<K> keys) {
        CollectionUtils.requireNonNull(keys, "keys", "key");
        if (keys.isEmpty()) {
            return Collections.emptySet();
        }
        return computeAllImpl(keys, (k, v) -> null).keySet();
    }

    @Override
    public final boolean remove(final K key) {
        Objects.requireNonNull(key, "key");
        return computeAllImpl(Collections.singleton(key), (k, v) -> null).containsKey(key);
    }

    protected abstract Map<K, V> getAllImpl(final Set<K> keys);

    protected abstract Map<K, V> computeAllImpl(final Set<K> keys, final BiFunction<K, V, V> operator);
}
