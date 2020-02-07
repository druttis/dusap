package org.dru.dusap.ecs;

import org.dru.dusap.util.Bag;

public final class Mapper<T> {
    private final int index;
    private final Class<T> type;
    private Bag<T> recycled;

    Mapper(final int index, final Class<T> type) {
        this.index = index;
        this.type = type;
        recycled = new Bag<>();
    }

    public int getIndex() {
        return index;
    }

    public Class<T> getType() {
        return type;
    }

    public T get(final Entity entity) {
        return type.cast(entity.getComponent(index));
    }

    public void set(final Entity entity, final T component) {
        entity.setComponent(index, component);
    }
}
