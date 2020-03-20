package org.dru.dusap.database;

import org.dru.dusap.database.config.DbConfigManager;
import org.dru.dusap.database.config.DbConfigManagerImpl;
import org.dru.dusap.database.executor.DbExecutorManager;
import org.dru.dusap.database.executor.DbExecutorManagerImpl;
import org.dru.dusap.database.model.DbTableFactory;
import org.dru.dusap.database.model.DbTableFactoryImpl;
import org.dru.dusap.database.model.DbTableManager;
import org.dru.dusap.database.model.DbTableManagerImpl;
import org.dru.dusap.database.pool.DbPoolManager;
import org.dru.dusap.database.pool.DbPoolManagerImpl;
import org.dru.dusap.database.store.DbBucketSupportManager;
import org.dru.dusap.database.store.DbBucketSupportManagerImpl;
import org.dru.dusap.database.store.DbStoreFactory;
import org.dru.dusap.database.store.DbStoreFactoryImpl;
import org.dru.dusap.database.type.DbTypes;
import org.dru.dusap.database.type.DbTypesImpl;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Module;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.store.StoreModule;
import org.dru.dusap.time.TimeModule;

@DependsOn({JsonModule.class, StoreModule.class, TimeModule.class})
public final class DatabaseModule extends Module {
    @Override
    protected void configure() {
        bind(DbConfigManager.class).toType(DbConfigManagerImpl.class).asSingleton();
        bind(DbExecutorManager.class).toType(DbExecutorManagerImpl.class).asSingleton();
        bind(DbTableFactory.class).toType(DbTableFactoryImpl.class).asSingleton();
        bind(DbTableManager.class).toType(DbTableManagerImpl.class).asSingleton();
        bind(DbPoolManager.class).toType(DbPoolManagerImpl.class).asSingleton();
        bind(DbBucketSupportManager.class).toType(DbBucketSupportManagerImpl.class).asSingleton();
        bind(DbStoreFactory.class).toType(DbStoreFactoryImpl.class).asSingleton();
        bind(DbTypes.class).toType(DbTypesImpl.class).asSingleton();
        expose(DbConfigManager.class);
        expose(DbExecutorManager.class);
        expose(DbTableFactory.class);
        expose(DbTableManager.class);
        expose(DbPoolManager.class);
        expose(DbBucketSupportManager.class);
        expose(DbStoreFactory.class);
        expose(DbTypes.class);
    }
}
