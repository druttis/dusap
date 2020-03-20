package org.dru.dusap.store;

public interface StoreFactory {
    <K, V> Store<K, V> newStore(Class<K> keyType, Class<V> valueType, int numBuckets, BucketFactory bucketFactory);
}
