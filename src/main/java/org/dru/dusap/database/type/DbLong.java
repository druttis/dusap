package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbLong extends AbstractDbType<Long> {
    public static final DbLong INSTANCE = new DbLong();

    private DbLong() {
        super(JDBCType.BIGINT);
    }

    @Override
    protected String getSQLImpl(final Long value) {
        return value.toString();
    }

    @Override
    protected Long getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getLong(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Long value)
            throws SQLException {
        stmt.setLong(index, value);
    }
}
