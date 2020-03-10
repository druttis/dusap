package org.dru.dusap.database.model;

import java.util.Objects;

public abstract class DbEntity<T> {
    private final String name;
    private final Class<T> type;

    protected DbEntity(final String name, final Class<T> type) {
        Objects.requireNonNull(name, "name");
        this.name = name;
        this.type = type;
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

    public abstract DbContext getContext();

    public abstract <R, D> R accept(DbEntityVisitor<R, D> visitor, D data);
}
