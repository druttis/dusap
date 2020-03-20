package org.dru.dusap.store;

import java.util.ArrayList;
import java.util.List;

public final class StoreFactoryImpl implements StoreFactory {
    @Override
    public <K, V> Store<K, V> newStore(final Class<K> keyType, final Class<V> valueType, final int numBuckets,
                                       final BucketFactory bucketFactory) {
        final List<Bucket<K, V>> buckets = new ArrayList<>();
        for (int bucketNum = 0; bucketNum < numBuckets; bucketNum++) {
            buckets.add(bucketFactory.newBucket(bucketNum));
        }
        return new DynamicStore<>(buckets);
    }
}
