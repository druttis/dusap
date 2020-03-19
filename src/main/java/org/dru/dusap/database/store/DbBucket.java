package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.store.AbstractBucket;
import org.dru.dusap.store.Row;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DbBucket<K, V> extends AbstractBucket<K, V> {
    private final DbExecutor dbExecutor;
    private final DbBucketSupport<K, V> dbBucketSupport;

    public DbBucket(final int num, final DbExecutor dbExecutor, final DbBucketSupport<K, V> dbBucketSupport) {
        super(num);
        Objects.requireNonNull(dbExecutor, "dbExecutor");
        Objects.requireNonNull(dbBucketSupport, "dbStoreSupport");
        this.dbExecutor = dbExecutor;
        this.dbBucketSupport = dbBucketSupport;
    }

    @Override
    public Map<K, Row<V>> select(final Set<K> keys, final boolean lock) {
        return dbBucketSupport.select(dbExecutor, keys, lock);
    }

    @Override
    public void begin() {
        dbExecutor.beginTransaction();
    }

    @Override
    public void upsert(final K key, final V value, final long modified) {
        dbBucketSupport.upsert(dbExecutor, key, value, modified);
    }

    @Override
    public void update(final K key, final V value, final long modified) {
        dbBucketSupport.update(dbExecutor, key, value, modified);
    }

    @Override
    public void delete(final K key, final long modified) {
        dbBucketSupport.delete(dbExecutor, key, modified);
    }

    @Override
    public void commit() {
        dbExecutor.commit();
    }

    @Override
    public void rollback() {
        dbExecutor.rollback();
    }
}
