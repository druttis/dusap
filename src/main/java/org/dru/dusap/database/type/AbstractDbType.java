package org.dru.dusap.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Objects;

public abstract class AbstractDbType<T> implements DbType<T> {
    private final SQLType sqlType;
    private final boolean variableLength;

    protected AbstractDbType(final SQLType sqlType, final boolean variableLength) {
        this.sqlType = Objects.requireNonNull(sqlType, "sqlType");
        this.variableLength = variableLength;
    }

    protected AbstractDbType(final SQLType sqlType) {
        this(sqlType, false);
    }

    @Override
    public final SQLType getSQLType() {
        return sqlType;
    }

    @Override
    public final boolean isVariableLength() {
        return variableLength;
    }

    @Override
    public final T getResult(final ResultSet rset, final int index, final T defaultValue) throws SQLException {
        final T result = getResultImpl(rset, index);
        return (result == null || rset.wasNull() ? defaultValue : result);
    }

    @Override
    public final T getResult(final ResultSet rset, final int index) throws SQLException {
        return getResult(rset, index, null);
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
