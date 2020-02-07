package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Entity;

public final class ComponentRemovedEvent extends EntityComponentEvent {
    public ComponentRemovedEvent(final Entity entity, final int index) {
        super(entity, index);
    }
}
