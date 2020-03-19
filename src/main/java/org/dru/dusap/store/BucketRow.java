package org.dru.dusap.store;

public final class BucketRow<K, V> {
    public static <K, V> BucketRow<K, V> create(final Bucket<K, V> shard, final V value, final long modified) {
        return new BucketRow<>(shard, value, modified);
    }

    public static <K, V> BucketRow<K, V> create() {
        return new BucketRow<>(null, null, 0L);
    }

    private final Bucket<K, V> bucket;
    private final V value;
    private final long modified;

    private BucketRow(final Bucket<K, V> bucket, final V value, final long modified) {
        this.bucket = bucket;
        this.value = value;
        this.modified = modified;
    }

    public Bucket<K, V> bucket() {
        return bucket;
    }

    public V value() {
        return value;
    }

    public long modified() {
        return modified;
    }

    @Override
    public String toString() {
        return value + " (" + modified + ") @ " + bucket;
    }
}
