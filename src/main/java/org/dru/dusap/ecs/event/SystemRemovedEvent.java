package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.EntitySystem;

public final class SystemRemovedEvent extends SystemEvent {
    public SystemRemovedEvent(final Engine source, final EntitySystem system) {
        super(source, system);
    }
}
