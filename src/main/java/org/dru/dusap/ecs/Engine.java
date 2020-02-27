package org.dru.dusap.ecs;

import org.dru.dusap.ecs.event.EntityAddedEvent;
import org.dru.dusap.ecs.event.EntityRemovedEvent;
import org.dru.dusap.ecs.event.SystemAddedEvent;
import org.dru.dusap.ecs.event.SystemRemovedEvent;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.event.EventSourceSupport;
import org.dru.dusap.util.Bag;
import org.dru.dusap.util.Bits;
import org.dru.dusap.util.Updatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class Engine extends EventSourceSupport implements Updatable {
    private final Map<Class<?>, Mapper<?>> mapperByType;
    private final Map<Aspect, Family> familyByAspect;
    private final Bag<Entity> entities;
    private final Bag<EntitySystem> systems;
    private final Bag<Runnable> pending;
    private boolean updating;

    public Engine(final EventBus eventBus) {
        super(eventBus);
        mapperByType = new HashMap<>();
        familyByAspect = new HashMap<>();
        entities = new Bag<>();
        systems = new Bag<>();
        pending = new Bag<>();
    }

    @SuppressWarnings("unchecked")
    public final <T> Mapper<T> mapper(final Class<T> type) {
        return (Mapper<T>) mapperByType.computeIfAbsent(type, $ -> new Mapper<>(mapperByType.size(), type));
    }

    public final AspectBuilder all(final Class<?>... types) {
        final AspectBuilder builder = new AspectBuilder(this);
        return builder.all(types);
    }

    public final AspectBuilder one(final Class<?>... types) {
        final AspectBuilder builder = new AspectBuilder(this);
        return builder.one(types);
    }

    public final AspectBuilder none(final Class<?>... types) {
        final AspectBuilder builder = new AspectBuilder(this);
        return builder.none(types);
    }

    final Bits bits(final Class<?>... types) {
        final Bits bits = new Bits();
        Stream.of(types).forEach(type -> bits.set(mapper(type).getIndex()));
        return bits;
    }

    public final Family family(final Aspect aspect) {
        return familyByAspect.computeIfAbsent(aspect, $ -> new Family(this, familyByAspect.size(), aspect));
    }

    public final Entity newEntity() {
        final Entity entity = new Entity(this, getEventBus());
        addEntity(entity);
        return entity;
    }

    private void addEntity(final Entity entity) {
        invoke(() -> addEntityInternally(entity));
    }

    private void addEntityInternally(final Entity entity) {
        entity.setEngineIndex(entities.size());
        entities.add(entity);
        fireEvent(new EntityAddedEvent(this, entity));
    }

    public final void removeEntity(final Entity entity) {
        invoke(() -> removeEntityInternally(entity));
    }

    private void removeEntityInternally(final Entity entity) {
        final int engineIndex = entity.getEngineIndex();
        if (engineIndex != -1) {
            entities.remove(engineIndex);
            entity.setEngineIndex(-1);
            if (engineIndex < entities.size()) {
                entities.get(engineIndex).setEngineIndex(engineIndex);
            }
            fireEvent(new EntityRemovedEvent(this, entity));
        }
    }

    public final void removeAllEntities() {
        invoke(this::removeAllEntitiesInternally);
    }

    private void removeAllEntitiesInternally() {
        while (entities.isEmpty()) {
            removeEntityInternally(entities.get(0));
        }
    }

    public void addSystem(final EntitySystem system) {
        Objects.requireNonNull(system, "system");
        invoke(() -> addSystemInternally(system));
    }

    private void addSystemInternally(final EntitySystem system) {
        final Engine engine = system.getEngine();
        if (engine != null) {
            engine.removeSystem(system);
        }
        systems.add(system);
        system.setEngine(engine);
        system.addedToEngine(this);
        fireEvent(new SystemAddedEvent(this, system));
    }

    public void removeSystem(final EntitySystem system) {
        if (system != null) {
            invoke(() -> removeSystemInternally(system));
        }
    }

    private void removeSystemInternally(final EntitySystem system) {
        if (systems.remove(system)) {
            system.setEngine(null);
            system.removedFromEngine(this);
            fireEvent(new SystemRemovedEvent(this, system));
        }
    }

    public boolean isUpdating() {
        return updating;
    }

    public void invoke(final Runnable task) {
        if (updating) {
            pending.add(task);
        } else {
            task.run();
        }
    }

    @Override
    public void update() {
        if (!updating) {
            updating = true;
            try {
                for (int index = 0; index < systems.size(); index++) {
                    systems.get(index).update();
                }
            } finally {
                updating = false;
                while (!pending.isEmpty()) {
                    pending.removeFirst().run();
                }
            }
        }
    }
}
