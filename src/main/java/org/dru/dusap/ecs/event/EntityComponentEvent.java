package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Entity;

public abstract class EntityComponentEvent extends EntityEvent {
    private int index;

    EntityComponentEvent(final Entity entity, final int index) {
        super(entity);
        this.index = index;
    }

    public final int getIndex() {
        return index;
    }
}
