package org.dru.dusap.store;

public interface BucketFactory {
    <K, V> Bucket<K, V> newBucket(int bucketNum);
}
