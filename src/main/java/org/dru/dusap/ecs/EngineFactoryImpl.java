package org.dru.dusap.ecs;

import org.dru.dusap.event.EventBus;

public final class EngineFactoryImpl implements EngineFactory {
    private final EventBus eventBus;

    public EngineFactoryImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Engine createEngine() {
        return new Engine(eventBus);
    }
}
