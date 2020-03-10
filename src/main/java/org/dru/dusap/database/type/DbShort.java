package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbShort extends AbstractDbType<Short> {
    public static final DbShort INSTANCE = new DbShort();

    private DbShort() {
        super(JDBCType.SMALLINT);
    }

    @Override
    protected String getSQLImpl(final Short value) {
        return value.toString();
    }

    @Override
    protected Short getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getShort(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Short value)
            throws SQLException {
        stmt.setShort(index, value);
    }
}
