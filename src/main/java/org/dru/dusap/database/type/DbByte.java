package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbByte extends AbstractDbType<Byte> {
    public static DbByte INSTANCE = new DbByte();

    private DbByte() {
        super(JDBCType.TINYINT);
    }

    @Override
    protected Byte getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getByte(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Byte value)
            throws SQLException {
        stmt.setByte(index, value);
    }
}
