package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;
import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

public final class DbColumn<C> extends DbEntity<C> {
    private static final Supplier<?> DEFAULT_DEFAULT_SUPPLIER = () -> null;

    @SuppressWarnings("unchecked")
    private static <T> Supplier<T> getDefaultDefaultSupplier() {
        return (Supplier<T>) DEFAULT_DEFAULT_SUPPLIER;
    }

    private final DbTable<?> table;
    private final DbType<C> dbType;
    private final Field field;
    private int length;
    private boolean notNull;
    private boolean primaryKey;
    private Supplier<C> defaultSupplier;

    public DbColumn(final DbTable<?> table, final String name, final Class<C> type, final Field field) {
        super(name, type);
        Objects.requireNonNull(table, "parent");
        this.table = table;
        this.dbType = getContext().getDbType(type);
        this.field = field;
        defaultUsing(null);
    }

    @Override
    public DbContext getContext() {
        return getTable().getContext();
    }

    @Override
    public <R, D> R accept(final DbEntityVisitor<R, D> visitor, final D data) {
        return visitor.visitColumn(this, data);
    }

    public DbTable<?> getTable() {
        return table;
    }

    public DbType<C> getDbType() {
        return dbType;
    }

    public Field getField() {
        return field;
    }

    public int getLength() {
        return length;
    }

    public DbColumn<C> length(final int length) {
        this.length = length;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public DbColumn<C> notNull() {
        notNull = true;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public DbColumn<C> primaryKey() {
        primaryKey = true;
        return this;
    }

    public Supplier<C> getDefaultSupplier() {
        return defaultSupplier;
    }

    public DbColumn<C> defaultUsing(final Supplier<C> defaultSupplier) {
        this.defaultSupplier = (defaultSupplier != null ? defaultSupplier : getDefaultDefaultSupplier());
        return this;
    }

    public DbColumn<C> defaultWith(final C value) {
        return defaultUsing(value != null ? () -> value : null);
    }

    public String getSQL(final C value) {
        return getDbType().getSQL(value);
    }

    public C getResult(final ResultSet rset, final int index) throws SQLException {
        return dbType.getResult(rset, index, getDefaultSupplier());
    }

    public void getResultInto(final ResultSet rset, final int index, final Object object) throws SQLException {
        ReflectionUtils.setField(object, getField(), getResult(rset, index));
    }

    public void setParameter(final PreparedStatement stmt, final int index, final C value) throws SQLException {
        dbType.setParameter(stmt, index, value);
    }

    public void setParameterFrom(final PreparedStatement stmt, final int index, final Object object)
            throws SQLException {
        setParameter(stmt, index, getType().cast(ReflectionUtils.getField(object, getField())));
    }

    @Override
    public String toString() {
        return getName();
    }
}
