package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.EntitySystem;

import java.util.Objects;

public abstract class SystemEvent extends EngineEvent {
    private final EntitySystem system;

    SystemEvent(final Engine source, final EntitySystem system) {
        super(source);
        Objects.requireNonNull(system, "system");
        this.system = system;
    }

    public EntitySystem getSystem() {
        return system;
    }
}
