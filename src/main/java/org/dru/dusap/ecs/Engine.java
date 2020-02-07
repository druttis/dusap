package org.dru.dusap.ecs;

import org.dru.dusap.ecs.event.EntityAddedEvent;
import org.dru.dusap.ecs.event.EntityRemovedEvent;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.event.EventSourceSupport;
import org.dru.dusap.util.Bag;
import org.dru.dusap.util.Bits;
import org.dru.dusap.util.Updatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class Engine extends EventSourceSupport implements Updatable {
    private final Map<Class<?>, Mapper<?>> mapperByType;
    private final Map<Aspect, Family> familyByAspect;
    private final Bag<Entity> entities;
    private final Bag<EntitySystem> systems;

    public Engine(final EventBus eventBus) {
        super(eventBus);
        mapperByType = new ConcurrentHashMap<>();
        familyByAspect = new ConcurrentHashMap<>();
        entities = new Bag<>();
        systems = new Bag<>();
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
        final Entity entity = new Entity(getEventBus());
        entity.setEngineIndex(entities.size());
        entities.add(entity);
        fireEvent(new EntityAddedEvent(this, entity));
        return entity;
    }

    public final void removeEntity(final Entity entity) {
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
        while (entities.isEmpty()) {
            removeEntity(entities.get(0));
        }
    }

    public void addSystem(final EntitySystem system) {
        final Engine engine = system.getEngine();
        if (engine != null) {
            engine.removeSystem(system);
        }
        systems.add(system);
        system.setEngine(engine);
    }

    public void removeSystem(final EntitySystem system) {
        if (systems.remove(system)) {
            system.setEngine(null);
        }
    }

    @Override
    public void update() {
        for (int index = 0; index < systems.size(); index++) {
            systems.get(index).update();
        }
    }
}
