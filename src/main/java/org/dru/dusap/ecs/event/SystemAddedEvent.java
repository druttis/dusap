package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;
import org.dru.dusap.ecs.EntitySystem;

public final class SystemAddedEvent extends SystemEvent {
    public SystemAddedEvent(final Engine source, final EntitySystem system) {
        super(source, system);
    }
}
