package org.dru.dusap.database.store;

public interface DbBucketSupportManager {
    <K, V> DbBucketSupport<K, V> getBucketSupport(final String tableName, final Class<K> keyType,
                                                  final Class<V> valueType);
}
