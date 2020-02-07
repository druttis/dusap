package org.dru.dusap.ecs;

import org.dru.dusap.ecs.event.ComponentAddedEvent;
import org.dru.dusap.ecs.event.ComponentRemovedEvent;
import org.dru.dusap.ecs.event.EntityAddedEvent;
import org.dru.dusap.ecs.event.EntityRemovedEvent;
import org.dru.dusap.util.Bag;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Family implements Iterable<Entity> {
    private final int index;
    private final Aspect aspect;
    private final Bag<Entity> members;

    Family(final Engine engine, final int index, final Aspect aspect) {
        this.index = index;
        this.aspect = aspect;
        members = new Bag<>();
        engine.addEventListener(EntityAddedEvent.class, this::onEntityAdded);
        engine.addEventListener(EntityRemovedEvent.class, this::onEntityRemoved);
    }

    @Override
    public Iterator<Entity> iterator() {
        return new Iterator<Entity>() {
            private int index;

            @Override
            public boolean hasNext() {
                return (index < members.size());
            }

            @Override
            public Entity next() {
                if (hasNext()) {
                    throw new NoSuchElementException();
                }
                return members.get(index++);
            }
        };
    }

    private void onEntityAdded(final EntityAddedEvent event) {
        final Entity entity = event.getEntity();
        if (aspect.matches(entity)) {
            addMember(entity);
            entity.addEventListener(ComponentRemovedEvent.class, this::onComponentRemoved);
        } else {
            entity.addEventListener(ComponentAddedEvent.class, this::onComponentAdded);
        }
    }

    private void onEntityRemoved(final EntityRemovedEvent event) {
        final Entity entity = event.getEntity();
        if (aspect.matches(entity)) {
            removeMember(entity);
            entity.removeEventListener(ComponentRemovedEvent.class, this::onComponentRemoved);
        }
    }

    private void onComponentAdded(final ComponentAddedEvent event) {
        final Entity entity = event.getSource();
        if (entity.getMemberIndex(index) != index && aspect.matches(entity)) {
            addMember(entity);
            entity.removeEventListener(ComponentAddedEvent.class, this::onComponentAdded);
            entity.addEventListener(ComponentRemovedEvent.class, this::onComponentRemoved);
        }
    }

    private void onComponentRemoved(final ComponentRemovedEvent event) {
        final Entity entity = event.getSource();
        if (entity.getMemberIndex(index) == index && !aspect.matches(entity)) {
            removeMember(entity);
            entity.removeEventListener(ComponentRemovedEvent.class, this::onComponentRemoved);
            entity.addEventListener(ComponentAddedEvent.class, this::onComponentAdded);
        }
    }

    private void addMember(final Entity member) {
        member.setMemberIndex(index, members.size());
        members.add(member);
    }

    private void removeMember(final Entity member) {
        final int memberIndex = member.getMemberIndex(index);
        if (memberIndex != -1) {
            members.remove(memberIndex);
            member.setMemberIndex(index, -1);
            if (memberIndex < members.size()) {
                members.get(memberIndex).setEngineIndex(memberIndex);
            }
        }
    }
}
