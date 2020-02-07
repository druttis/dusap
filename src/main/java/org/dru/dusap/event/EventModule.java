package org.dru.dusap.event;

import org.dru.dusap.injection.Module;

public final class EventModule extends Module {
    public EventModule() {
    }

    @Override
    protected void configure() {
        bind(EventBus.class).toType(EventBusImpl.class).asSingleton();
        expose(EventBus.class);
    }
}
