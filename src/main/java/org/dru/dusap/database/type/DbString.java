package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbString extends AbstractDbType<String> {
    public static final DbString INSTANCE = new DbString();

    private DbString() {
        super(JDBCType.VARCHAR, true, true);
    }

    @Override
    protected String getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getString(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final String value)
            throws SQLException {
        stmt.setString(index, value);
    }
}
