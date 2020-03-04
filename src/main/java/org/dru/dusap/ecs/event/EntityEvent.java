package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Entity;

import java.util.Objects;

public abstract class EntityEvent  {
    private final Entity source;

    EntityEvent(final Entity source) {
        Objects.requireNonNull(source, "source");
        this.source = source;
    }

    public final Entity getSource() {
        return source;
    }
}
