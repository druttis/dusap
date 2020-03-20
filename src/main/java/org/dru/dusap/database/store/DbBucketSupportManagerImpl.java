package org.dru.dusap.database.store;

import org.dru.dusap.database.model.DbTableFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DbBucketSupportManagerImpl implements DbBucketSupportManager {
    private final DbTableFactory dbTableFactory;
    private final Map<Key, DbBucketSupport<?, ?>> bucketSupportByKey;

    public DbBucketSupportManagerImpl(final DbTableFactory dbTableFactory) {
        this.dbTableFactory = dbTableFactory;
        bucketSupportByKey = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> DbBucketSupport<K, V> getBucketSupport(final String tableName, final Class<K> keyType,
                                                         final Class<V> valueType) {
        final Key key = new Key(tableName, keyType, valueType);
        return (DbBucketSupport<K, V>) bucketSupportByKey.computeIfAbsent(key, ($) ->
                new DbBucketSupport<>(dbTableFactory, tableName, keyType, valueType));
    }

    private static class Key {
        private final String tableName;
        private final Class<?> keyType;
        private final Class<?> valueType;

        private Key(final String tableName, final Class<?> keyType, final Class<?> valueType) {
            this.tableName = tableName;
            this.keyType = keyType;
            this.valueType = valueType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            final Key key = (Key) o;
            return tableName.equals(key.tableName) &&
                    keyType.equals(key.keyType) &&
                    valueType.equals(key.valueType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableName, keyType, valueType);
        }
    }
}
