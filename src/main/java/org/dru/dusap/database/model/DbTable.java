package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DbTable<T> extends DbEntity<T> {
    private final DbContext context;
    private final Constructor<T> constructor;

    DbTable(final DbContext context, final String name, final Class<T> type) {
        super(name, type);
        Objects.requireNonNull(context, "context");
        this.context = context;
        if (type != null) {
            constructor = ReflectionUtils.getDefaultConstructor(type);
            explodeInternal();
            if (!hasColumns()) {
                throw new IllegalStateException("type has no serializable fields: type=" + type.getName());
            }
        } else {
            constructor = null;
        }
    }

    @Override
    public DbContext getContext() {
        return context;
    }

    @Override
    public DbEntity<?> getParent() {
        return null;
    }

    @Override
    public DbEntity<?> getRoot() {
        return this;
    }

    @Override
    public <R, D> R accept(final DbVisitor<R, D> visitor, final D data) {
        return visitor.visitTable(this, data);
    }

    @Override
    protected String getColumnName(final String name) {
        return name;
    }

    public DbTable<T> defaultUsing(final Supplier<T> supplier) {
        setDefaultUsing(supplier);
        return this;
    }

    public DbTable<T> defaultWith(final T value) {
        setDefaultWith(value);
        return this;
    }

    public <C> DbColumn<C> newColumn(final String name, final Class<C> type) {
        final DbColumn<C> column = new DbColumn<>(this, name, type, null);
        addColumnInternal(column);
        return column;
    }

    public final Constructor<T> getConstructor() {
        return constructor;
    }

    public List<DbColumn<?>> getDbColumns() {
        return getColumns().stream().flatMap(column -> column.getDbColumns().stream()).collect(Collectors.toList());
    }
}
