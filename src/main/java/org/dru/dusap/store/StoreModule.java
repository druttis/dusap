package org.dru.dusap.store;

import org.dru.dusap.injection.Module;

public final class StoreModule extends Module {
    @Override
    protected void configure() {
        bind(StoreFactory.class).toType(StoreFactoryImpl.class).asSingleton();
        expose(StoreFactory.class);
    }
}
