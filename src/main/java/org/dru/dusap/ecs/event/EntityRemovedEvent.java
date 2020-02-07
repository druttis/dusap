package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.Entity;

public final class EntityRemovedEvent extends EngineEntityEvent {
    public EntityRemovedEvent(final Engine engine, final Entity entity) {
        super(engine, entity);
    }
}
