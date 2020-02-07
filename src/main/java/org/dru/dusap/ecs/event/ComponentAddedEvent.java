package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Entity;

public final class ComponentAddedEvent extends EntityComponentEvent {
    public ComponentAddedEvent(final Entity entity, final int index) {
        super(entity, index);
    }
}
