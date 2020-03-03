package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;
import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DbMember<T> extends DbEntity<T> {
    private static <T> Constructor<T> getDefaultConstructor(final Class<T> type) {
        try {
            return ReflectionUtils.getDefaultConstructor(type);
        } catch (final RuntimeException exc) {
            return null;
        }
    }

    private final DbTable<?> table;
    private final DbEntity<?> parent;
    private final Field field;
    private final Constructor<T> constructor;
    private final DbType<T> dbType;
    private int length;
    private boolean notNull;
    private boolean primaryKey;

    DbMember(final DbContext context, final DbTable<?> table, final DbEntity<?> parent, final String name,
             final Class<T> type, final Field field) {
        super(context, name, Objects.requireNonNull(type, "type"));
        this.table = Objects.requireNonNull(table, "table");
        this.parent = Objects.requireNonNull(parent, "parent");
        this.field = field;
        constructor = getDefaultConstructor(getType());
        dbType = getContext().getDbType(getType());
    }

    @Override
    public DbTable<?> getTable() {
        return table;
    }

    @Override
    public final List<DbMember<?>> getColumns() {
        if (hasMembers()) {
            return getMembers().stream()
                    .flatMap(member -> member.getColumns().stream())
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(this);
        }
    }

    @Override
    public T getResult(final ResultSet rset, final int index) throws SQLException {
        if (hasMembers()) {
            final T result = ReflectionUtils.newInstance(getConstructor());
            getResultInto(rset, index, result);
            return result;
        } else {
            return getDbType().getResult(rset, index, getDefaultSupplier());
        }
    }

    @Override
    public String getDDL() {
        final StringBuilder sb = new StringBuilder(getDbName());
        sb.append(' ');
        sb.append(getDbType().getDDL(getLength()));
        if (isNotNull() && !isPrimaryKey()) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setParameterRaw(final PreparedStatement stmt, final int index,
                                   final Object value) throws SQLException {
        if (hasMembers()) {
            setParameterFrom(stmt, index, (T) value);
        } else {
            getDbType().setParameter(stmt, index, (T) value);
        }
    }

    public DbEntity<?> getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public DbType<T> getDbType() {
        return dbType;
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
        notNull = true;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public DbMember<T> primaryKey() {
        primaryKey = true;
        return this;
    }

    public DbMember<T> defaultSupplier(final Supplier<T> supplier) {
        setDefaultSupplier(supplier);
        return this;
    }

    public DbMember<T> defaultValue(final T value) {
        setDefaultValue(value);
        return this;
    }

    public void explode(final boolean recursive) {
        if (hasMembers()) {
            throw new IllegalStateException("already exploded");
        } else if (getConstructor() != null) {
            ReflectionUtils.getSerializableFields(getType()).forEach(field ->
                    addMember(new DbMember<>(getContext(), getTable(), this, field.getName(), field.getType(), field)));
            if (recursive) {
                getMembers().forEach(member -> member.explode(true));
            }
        }
    }

    public void explode() {
        explode(false);
    }
}
