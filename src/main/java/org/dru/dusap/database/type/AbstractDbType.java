package org.dru.dusap.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractDbType<T> implements DbType<T> {
    private static final Supplier<?> NULL_SUPPLIER = () -> null;

    @SuppressWarnings("unchecked")
    private static <T> Supplier<T> getNullSupplier() {
        return (Supplier<T>) NULL_SUPPLIER;
    }

    private final SQLType sqlType;
    private final boolean primitive;
    private final boolean variableLength;

    protected AbstractDbType(final SQLType sqlType, final boolean primitive, final boolean variableLength) {
        this.sqlType = Objects.requireNonNull(sqlType, "sqlType");
        this.primitive = primitive;
        this.variableLength = variableLength;
    }

    protected AbstractDbType(final SQLType sqlType) {
        this(sqlType, true, false);
    }

    @Override
    public final SQLType getSQLType() {
        return sqlType;
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public final boolean isVariableLength() {
        return variableLength;
    }

    @Override
    public final T fetchOrDefault(final ResultSet rset, final int index, final Supplier<T> supplier)
            throws SQLException {
        Objects.requireNonNull(rset, "rset");
        Objects.requireNonNull(supplier, "supplier");
        final T result = getResultImpl(rset, index);
        return (result == null || rset.wasNull() ? supplier.get() : result);
    }

    @Override
    public final T fetch(final ResultSet rset, final int index) throws SQLException {
        return fetchOrDefault(rset, index, getNullSupplier());
    }

    @Override
    public final void setParameter(final PreparedStatement stmt, final int index, final T value)
            throws SQLException {
        if (value != null) {
            setParameterImpl(stmt, index, value);
        } else {
            stmt.setNull(index, getSQLType().getVendorTypeNumber());
        }
    }

    @Override
    public final String getDDL(final int length) {
        if (isVariableLength()) {
            return String.format("%s(%d)", getSQLType().getName(), length);
        } else {
            return getSQLType().getName();
        }
    }

    protected abstract T getResultImpl(ResultSet rset, int index) throws SQLException;

    protected abstract void setParameterImpl(PreparedStatement stmt, int index, T value) throws SQLException;
}
