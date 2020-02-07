package org.dru.dusap.ecs.event;

import org.dru.dusap.ecs.Engine;

import java.util.Objects;

public abstract class EngineEvent {
    private final Engine source;

    EngineEvent(final Engine source) {
        Objects.requireNonNull(source, "engine");
        this.source = source;
    }

    public final Engine getSource() {
        return source;
    }
}
