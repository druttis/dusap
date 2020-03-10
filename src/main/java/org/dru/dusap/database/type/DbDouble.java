package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbDouble extends AbstractDbType<Double> {
    public static final DbDouble INSTANCE = new DbDouble();

    private DbDouble() {
        super(JDBCType.DOUBLE);
    }

    @Override
    protected String getSQLImpl(final Double value) {
        return value.toString();
    }

    @Override
    protected Double getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getDouble(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Double value)
            throws SQLException {
        stmt.setDouble(index, value);
    }
}
