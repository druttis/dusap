package org.dru.dusap.store;

import org.dru.dusap.reflection.ReflectionUtils;

import java.util.*;
import java.util.function.BiFunction;

public final class SimpleStore<K, V> extends AbstractStore<K, V> {
    private final Bucket<K, V> bucket;

    public SimpleStore(final Bucket<K, V> bucket) {
        Objects.requireNonNull(bucket, "bucket");
        this.bucket = bucket;
    }

    @Override
    protected Map<K, V> getAllImpl(final Set<K> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<K, Row<V>> rows = bucket.select(keys, false);
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
        bucket.begin();
        try {
            final Map<K, Row<V>> rows = bucket.select(keys, true);
            for (final K key : keys) {
                final Row<V> row = rows.getOrDefault(key, Row.create());
                final V oldValue = row.value();
                final V newValue = operator.apply(key, ReflectionUtils.copyInstance(oldValue));
                if (newValue != null) {
                    result.put(key, newValue);
                    if (!newValue.equals(oldValue)) {
                        bucket.upsert(key, newValue, row.modified() + 1);
                    }
                } else if (oldValue != null) {
                    bucket.delete(key, row.modified() + 1);
                }
            }
            bucket.commit();
        } catch (final Exception exc) {
            bucket.rollback();
            throw exc;
        }
        return result;
    }
}
