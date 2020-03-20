package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.database.executor.DbExecutorManager;
import org.dru.dusap.database.model.DbTableManager;
import org.dru.dusap.database.pool.DbPoolManager;
import org.dru.dusap.store.Store;
import org.dru.dusap.store.StoreFactory;

public final class DbStoreFactoryImpl implements DbStoreFactory {
    private final DbBucketSupportManager dbBucketSupportManager;
    private final DbPoolManager dbPoolManager;
    private final DbExecutorManager dbExecutorManager;
    private final DbTableManager dbTableManager;
    private final StoreFactory storeFactory;

    public DbStoreFactoryImpl(final DbBucketSupportManager dbBucketSupportManager, final DbPoolManager dbPoolManager,
                              final DbExecutorManager dbExecutorManager, final DbTableManager dbTableManager,
                              final StoreFactory storeFactory) {
        this.dbBucketSupportManager = dbBucketSupportManager;
        this.dbPoolManager = dbPoolManager;
        this.dbExecutorManager = dbExecutorManager;
        this.dbTableManager = dbTableManager;
        this.storeFactory = storeFactory;
    }

    @Override
    public <K, V> Store<K, V> newStore(final String configName, final String tableName, final Class<K> keyType,
                                       final Class<V> valueType) {
        final DbBucketSupport<K, V> dbBucketSupport
                = dbBucketSupportManager.getBucketSupport(tableName, keyType, valueType);
        final int numBuckets = dbPoolManager.getPools(configName).size();
        final DbExecutor dbExecutor = dbExecutorManager.getExecutor(configName);
        final DbBucketFactory bucketFactory = new DbBucketFactory(dbExecutor, dbTableManager, dbBucketSupport);
        return storeFactory.newStore(keyType, valueType, numBuckets, bucketFactory);
    }
}
