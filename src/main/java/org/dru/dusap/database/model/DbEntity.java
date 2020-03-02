package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class DbEntity<T> {
    private static final Supplier<?> NULL_SUPPLIER = () -> null;

    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> getNullSupplier() {
        return (Supplier<T>) NULL_SUPPLIER;
    }

    private String name;
    private final Class<T> type;
    private Supplier<T> supplier;
    private final List<DbColumn<?>> columns;

    protected DbEntity(final String name, final Class<T> type) {
        setName(name);
        this.type = type;
        columns = new ArrayList<>();
    }

    protected final void setName(final String name) {
        Objects.requireNonNull(name, "name");
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final String getDbName() {
        return String.format("`%s`", getName());
    }

    public final Class<T> getType() {
        return type;
    }

    public final Supplier<T> getSupplier() {
        return supplier;
    }

    public final List<DbColumn<?>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public final boolean hasColumns() {
        return !getColumns().isEmpty();
    }

    public final DbColumn<?> getColumn(final String name) {
        final DbColumn<?> column = getColumnOrNull(name);
        if (column == null) {
            throw new IllegalArgumentException("no such column: name=" + name);
        }
        return column;
    }

    @SuppressWarnings("unchecked")
    public final <C> DbColumn<C> getColumn(final String name, final Class<C> type) {
        Objects.requireNonNull(type, "type");
        final DbColumn<?> column = getColumn(name);
        if (!column.getType().equals(type)) {
            throw new IllegalArgumentException("column type mismatch: name=" + name + ", type=" + type.getName());
        }
        return (DbColumn<C>) column;
    }


    protected final void setDefaultUsing(final Supplier<T> supplier) {
        this.supplier = (supplier != null ? supplier : getNullSupplier());
    }

    protected final void setDefaultWith(final T value) {
        setDefaultUsing(value != null ? () -> value : null);
    }

    protected DbColumn<?> getColumnOrNull(final String name) {
        Objects.requireNonNull(name, "name");
        for (final DbColumn<?> column : getColumns()) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    protected final void addColumnInternal(final DbColumn<?> column) {
        Objects.requireNonNull(column, "column");
        final String name = column.getName();
        final DbColumn<?> existing = getColumnOrNull(name);
        if (existing != null) {
            throw new IllegalArgumentException("duplicate column: name=" + name);
        }
        columns.add(column);
    }

    protected final void explodeInternal() {
        if (hasColumns()) {
            throw new IllegalStateException("already exploded");
        }
        ReflectionUtils.getSerializableFields(getType()).forEach(field -> {
            final String name = getColumnName(field.getName());
            final Class<?> type = field.getType();
            addColumnInternal(new DbColumn<>(this, name, type, field));
        });
    }

    public abstract DbContext getContext();

    public abstract DbEntity<?> getParent();

    public abstract DbEntity<?> getRoot();

    public abstract <R, D> R accept(DbVisitor<R, D> visitor, D data);

    protected abstract String getColumnName(String name);
}
