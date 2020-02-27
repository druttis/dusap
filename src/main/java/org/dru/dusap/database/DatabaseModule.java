package org.dru.dusap.database;

import org.dru.dusap.database.executor.DbExecutorProvider;
import org.dru.dusap.database.executor.DbExecutorProviderImpl;
import org.dru.dusap.database.model.DbFactory;
import org.dru.dusap.database.model.DbFactoryImpl;
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
        bind(DbExecutorProvider.class).toType(DbExecutorProviderImpl.class).asSingleton();
        bind(DbFactory.class).toType(DbFactoryImpl.class).asSingleton();
        bind(DbPoolManager.class).toType(DbPoolManagerImpl.class).asSingleton();
        bind(DbTypes.class).toType(DbTypesImpl.class).asSingleton();
        expose(DbExecutorProvider.class);
        expose(DbFactory.class);
        expose(DbPoolManager.class);
        expose(DbTypes.class);
    }
}
