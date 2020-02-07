package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbBoolean extends AbstractDbType<Boolean> {
    public static final DbBoolean INSTANCE = new DbBoolean();

    private DbBoolean() {
        super(JDBCType.BIT);
    }

    @Override
    protected Boolean getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getBoolean(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Boolean value)
            throws SQLException {
        stmt.setBoolean(index, value);
    }
}
