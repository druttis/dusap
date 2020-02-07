package org.dru.dusap.ecs;

import org.dru.dusap.util.Updatable;

public abstract class EntitySystem implements Updatable {
    private Engine engine;

    public final Engine getEngine() {
        return engine;
    }

    final void setEngine(final Engine engine) {
        final Engine old = getEngine();
        if (engine != old) {
            this.engine = engine;
            if (old != null) {
                removedFromEngine(old);
            }
            if (engine != null) {
                addedToEngine(engine);
            }
        }
    }

    protected void addedToEngine(final Engine engine) {
    }

    protected void removedFromEngine(final Engine engine) {
    }
}
