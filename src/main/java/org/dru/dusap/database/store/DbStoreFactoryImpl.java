package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.database.executor.DbExecutorProvider;
import org.dru.dusap.database.model.DbFactory;
import org.dru.dusap.database.model.DbTableManager;
import org.dru.dusap.time.TimeSupplier;

public final class DbStoreFactoryImpl implements DbStoreFactory {
    private final DbExecutorProvider dbExecutorProvider;
    private final TimeSupplier timeSupplier;
    private final DbFactory factory;
    private final DbTableManager dbTableManager;

    public DbStoreFactoryImpl(final DbExecutorProvider dbExecutorProvider, final TimeSupplier timeSupplier,
                              final DbFactory factory, final DbTableManager dbTableManager) {
        this.dbExecutorProvider = dbExecutorProvider;
        this.timeSupplier = timeSupplier;
        this.factory = factory;
        this.dbTableManager = dbTableManager;
    }

    @Override
    public <K, V> DbStore<K, V> newStore(final String name, final Class<K> keyType, final Class<V> valueType) {
        final DbExecutor dbExecutor = dbExecutorProvider.getExecutor("store");
        return new DbStoreImpl<>(dbExecutor, timeSupplier, name, keyType, valueType, factory, dbTableManager);
    }
}
