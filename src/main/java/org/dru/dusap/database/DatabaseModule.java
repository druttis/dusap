package org.dru.dusap.database;

import org.dru.dusap.database.executor.DbExecutorManager;
import org.dru.dusap.database.executor.DbExecutorManagerImpl;
import org.dru.dusap.database.model.DbTableFactory;
import org.dru.dusap.database.model.DbTableFactoryImpl;
import org.dru.dusap.database.model.DbTableManager;
import org.dru.dusap.database.model.DbTableManagerImpl;
import org.dru.dusap.database.pool.DbPoolManager;
import org.dru.dusap.database.pool.DbPoolManagerImpl;
import org.dru.dusap.database.type.DbTypes;
import org.dru.dusap.database.type.DbTypesImpl;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Module;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.time.TimeModule;

@DependsOn({JsonModule.class, TimeModule.class})
public final class DatabaseModule extends Module {
    @Override
    protected void configure() {
        bind(DbExecutorManager.class).toType(DbExecutorManagerImpl.class).asSingleton();
        bind(DbTableFactory.class).toType(DbTableFactoryImpl.class).asSingleton();
        bind(DbPoolManager.class).toType(DbPoolManagerImpl.class).asSingleton();
        bind(DbTableManager.class).toType(DbTableManagerImpl.class).asSingleton();
        bind(DbTypes.class).toType(DbTypesImpl.class).asSingleton();
        expose(DbExecutorManager.class);
        expose(DbTableFactory.class);
        expose(DbPoolManager.class);
        expose(DbTableManager.class);
        expose(DbTypes.class);
    }
}
