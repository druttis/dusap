package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public final class DbMember<T> extends DbEntity<T> implements DbContainer {
    private final DbType<T> dbType;
    private final Field field;
    private final DbSupport support;
    private int length;
    private boolean notNull;
    private boolean primaryKey;

    DbMember(final DbEntity<?> parent, final String name, final Class<T> type, final Field field) {
        super(parent, name, type);
        Objects.requireNonNull(parent, "parent");
        dbType = getContext().getDbType(type);
        this.field = field;
        support = new DbSupport(this);
    }

    @Override
    public DbContext getContext() {
        return getParent().getContext();
    }

    @Override
    public String getQualifiedName(final String name) {
        Objects.requireNonNull(name, "name");
        return String.format("%s_%s", getQualifiedName(), name);
    }

    @Override
    public String getDDL() {
        final StringBuilder sb = new StringBuilder(getDbName());
        sb.append(' ');
        sb.append(dbType.getDDL(length));
        if (notNull && !primaryKey) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    @Override
    public DbTable<?> getTable() {
        return getParent().getTable();
    }

    @Override
    public boolean hasMembers() {
        return support.hasMembers();
    }

    @Override
    public List<DbMember<?>> getMembers() {
        return support.getMembers();
    }

    @Override
    public <F> DbMember<F> getMember(final String name) {
        return support.getMember(name);
    }

    @Override
    public <F> DbMember<F> newMember(final String name, final Class<F> type) {
        return support.newMember(name, type);
    }

    @Override
    public List<DbMember<?>> getColumns() {
        return support.getColumns();
    }

    public Field getField() {
        return field;
    }

    public int getLength() {
        return length;
    }

    public DbMember<T> length(final int length) {
        this.length = length;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public DbMember<T> notNull() {
        this.notNull = true;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public DbMember<T> primaryKey() {
        if (!primaryKey) {
            primaryKey = true;
            if (!dbType.isPrimitive()) {
                support.populateMembers(getType());
                getMembers().forEach(DbMember::primaryKey);
            }
        }
        return this;
    }

    public void explode() {
        if (hasMembers()) {
            throw new IllegalStateException("already exploded");
        }
        if (dbType.isPrimitive()) {
            throw new IllegalStateException("can not explode primitives");
        }
        support.populateMembers(getType());
    }
}
