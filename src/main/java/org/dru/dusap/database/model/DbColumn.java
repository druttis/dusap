package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DbColumn<T> extends DbEntity<T> {
    private final DbEntity<?> parent;
    private final Field field;
    private final DbType<T> dbType;
    private int length;
    private boolean notNull;
    private boolean primaryKey;

    DbColumn(final DbEntity<?> parent, final String name, final Class<T> type, final Field field) {
        super(name, type);
        Objects.requireNonNull(parent, "parent");
        this.parent = parent;
        this.field = field;
        dbType = getContext().getType(type);
    }

    @Override
    public DbContext getContext() {
        return getRoot().getContext();
    }

    @Override
    public DbEntity<?> getParent() {
        return parent;
    }

    @Override
    public DbEntity<?> getRoot() {
        return getParent().getRoot();
    }

    @Override
    public String getQualifiedDbName() {
        return String.format("`%s.%s`", getRoot().getName(), getName());
    }

    @Override
    public <R, D> R accept(final DbVisitor<R, D> visitor, final D data) {
        return visitor.visitColumn(this, data);
    }

    @Override
    protected String getColumnName(final String name) {
        return String.format("%s_%s", getName(), name);
    }

    public Field getField() {
        return field;
    }

    public DbType<T> getDbType() {
        return dbType;
    }

    public int getLength() {
        return length;
    }

    public DbColumn<T> length(final int length) {
        this.length = length;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public DbColumn<T> notNull() {
        notNull = true;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public DbColumn<T> setPrimaryKey() {
        primaryKey = true;
        return this;
    }

    public DbColumn<T> explode(final boolean recursive) {
        explodeInternal();
        if (recursive) {
            getColumns().forEach(column -> column.explode(true));
        }
        return this;
    }

    public DbColumn<T> explode() {
        return explode(false);
    }

    public boolean isDbColumn() {
        return !hasColumns();
    }

    public List<DbColumn<?>> getDbColumns() {
        if (isDbColumn()) {
            return Collections.singletonList(this);
        } else {
            return getColumns().stream().flatMap(column -> column.getDbColumns().stream()).collect(Collectors.toList());
        }
    }
}
