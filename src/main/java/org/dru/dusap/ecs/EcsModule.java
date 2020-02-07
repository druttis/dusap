package org.dru.dusap.ecs;

import org.dru.dusap.event.EventModule;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Module;

@DependsOn(EventModule.class)
public final class EcsModule extends Module {
    public EcsModule() {
    }

    @Override
    protected void configure() {
        bind(EngineFactory.class).toType(EngineFactoryImpl.class).asSingleton();
        expose(EngineFactory.class);
    }
}
