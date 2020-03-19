package org.dru.dusap.store;

public abstract class AbstractBucket<K, V> implements Bucket<K, V> {
    private final int num;

    protected AbstractBucket(final int num) {
        this.num = num;
    }

    @Override
    public final int num() {
        return num;
    }

    @Override
    public String toString() {
        return getClass().getName() + "#" + num;
    }
}
