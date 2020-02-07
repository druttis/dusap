package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbInteger extends AbstractDbType<Integer> {
    public static final DbInteger INSTANCE = new DbInteger();

    private DbInteger() {
        super(JDBCType.INTEGER);
    }

    @Override
    protected Integer getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getInt(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Integer value)
            throws SQLException {
        stmt.setInt(index, value);
    }
}
