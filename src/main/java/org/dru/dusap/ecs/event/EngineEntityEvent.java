package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.Entity;

import java.util.Objects;

public abstract class EngineEntityEvent extends EngineEvent {
    private Entity entity;

    EngineEntityEvent(final Engine engine, final Entity entity) {
        super(engine);
        Objects.requireNonNull(entity, "entity");
        this.entity = entity;
    }

    public final Entity getEntity() {
        return entity;
    }
}
