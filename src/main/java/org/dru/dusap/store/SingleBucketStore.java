package org.dru.dusap.store;

import org.dru.dusap.reflection.ReflectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public final class SingleBucketStore<K, V> extends AbstractStore<K, V> {
    private final Bucket<K, V> shard;

    public SingleBucketStore(final Bucket<K, V> shard) {
        this.shard = shard;
    }

    @Override
    protected Map<K, V> getAllImpl(final Set<K> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<K, Row<V>> rows = shard.select(keys, false);
        final Map<K, V> result = new HashMap<>();
        for (final K key : keys) {
            final Row<V> row = rows.get(key);
            if (row != null) {
                result.put(key, row.value());
            }
        }
        return result;
    }

    @Override
    protected Map<K, V> computeAllImpl(final Set<K> keys, final BiFunction<K, V, V> operator) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<K, V> result = new HashMap<>();
        shard.begin();
        try {
            final Map<K, Row<V>> rows = shard.select(keys, true);
            for (final K key : keys) {
                final Row<V> row = rows.getOrDefault(key, Row.create());
                final V oldValue = row.value();
                final V newValue = operator.apply(key, ReflectionUtils.copyInstance(oldValue));
                if (newValue != null) {
                    result.put(key, newValue);
                    if (!newValue.equals(oldValue)) {
                        shard.update(key, newValue, row.modified() + 1);
                    }
                } else if (oldValue != null) {
                    shard.delete(key, row.modified() + 1);
                }
            }
            shard.commit();
        } catch (final Exception exc) {
            shard.rollback();
            throw exc;
        }
        return result;
    }
}
