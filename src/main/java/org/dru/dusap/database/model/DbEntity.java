package org.dru.dusap.database.model;

import java.util.Objects;

public abstract class DbEntity<T> {
    private final DbEntity<?> parent;
    private final String name;
    private final Class<T> type;

    public DbEntity(final DbEntity<?> parent, final String name, final Class<T> type) {
        Objects.requireNonNull(name, "name");
        this.parent = parent;
        this.name = name;
        this.type = type;
    }

    public final DbEntity<?> getParent() {
        return parent;
    }

    public final String getName() {
        return name;
    }

    public final String getDbName() {
        return String.format("`%s`", getName());
    }

    public final String getQualifiedName() {
        return (parent != null ? parent.getQualifiedName(name) : name);
    }

    public final String getQualifiedDbName() {
        return String.format("`%s`", getQualifiedDbName());
    }

    public final Class<T> getType() {
        return type;
    }

    public abstract DbContext getContext();

    public abstract String getQualifiedName(String name);

    public abstract String getDDL();

    public abstract DbTable<?> getTable();
}
