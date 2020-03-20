package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.database.model.DbTableManager;
import org.dru.dusap.store.BucketFactory;

public final class DbBucketFactory implements BucketFactory {
    private final DbExecutor dbExecutor;
    private final DbTableManager dbTableManager;
    private final DbBucketSupport<?, ?> dbBucketSupport;

    public DbBucketFactory(final DbExecutor dbExecutor, final DbTableManager dbTableManager,
                           final DbBucketSupport<?, ?> dbBucketSupport) {
        this.dbExecutor = dbExecutor;
        this.dbTableManager = dbTableManager;
        this.dbBucketSupport = dbBucketSupport;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> DbBucket<K, V> newBucket(final int bucketNum) {
        dbTableManager.createTableIfNotExist(dbExecutor, bucketNum, dbBucketSupport.getDbTable());
        return new DbBucket<>(bucketNum, dbExecutor, (DbBucketSupport<K, V>) dbBucketSupport);
    }
}
