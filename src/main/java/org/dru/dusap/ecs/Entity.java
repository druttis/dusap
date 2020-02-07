package org.dru.dusap.ecs;

import org.dru.dusap.ecs.event.ComponentAddedEvent;
import org.dru.dusap.ecs.event.ComponentRemovedEvent;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.event.EventSourceSupport;
import org.dru.dusap.util.Bits;

import java.util.Arrays;

public final class Entity extends EventSourceSupport {
    private final Bits componentBits;
    private Object[] components;
    private int engineIndex;
    private int[] memberIndexes;

    public Entity(final EventBus eventBus) {
        super(eventBus);
        componentBits = new Bits();
        components = new Object[4];
    }

    Bits getComponentBits() {
        return componentBits;
    }

    public Object getComponent(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (index < components.length ? components[index] : null);
    }

    public Object setComponent(final int index, final Object component) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final Object old;
        if (component != null) {
            if (index >= components.length) {
                components = Arrays.copyOf(components, (index * 3) / 2 + 1);
            }
            componentBits.set(index);
            old = components[index];
            components[index] = component;
            if (old == null) {
                fireEvent(new ComponentAddedEvent(this, index));
            }
        } else if (index < components.length) {
            componentBits.clear(index);
            old = components[index];
            components[index] = null;
            if (old != null) {
                fireEvent(new ComponentRemovedEvent(this, index));
            }
        } else {
            old = null;
        }
        return old;
    }

    int getEngineIndex() {
        return engineIndex;
    }

    void setEngineIndex(final int engineIndex) {
        this.engineIndex = engineIndex;
    }

    int getMemberIndex(final int familyIndex) {
        return (familyIndex < memberIndexes.length ? memberIndexes[familyIndex] : -1);
    }

    void setMemberIndex(final int familyIndex, final int memberIndex) {
        if (memberIndex != -1) {
            if (familyIndex >= memberIndexes.length) {
                memberIndexes = Arrays.copyOf(memberIndexes, familyIndex * 3 / 2 + 1);
            }
            memberIndexes[familyIndex] = memberIndex;
        } else {
            if (familyIndex < memberIndexes.length) {
                memberIndexes[familyIndex] = -1;
            }
        }
    }
}
