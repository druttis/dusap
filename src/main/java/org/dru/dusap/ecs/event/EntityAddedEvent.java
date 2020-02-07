package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.Entity;

public final class EntityAddedEvent extends EngineEntityEvent {
    public EntityAddedEvent(final Engine engine, final Entity entity) {
        super(engine, entity);
    }
}
