package org.dru.dusap.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbCharacter extends AbstractDbType<Character> {
    public static final DbCharacter INSTANCE = new DbCharacter();

    private DbCharacter() {
        super(JDBCType.CHAR);
    }

    @Override
    protected String getSQLImpl(final Character value) {
        return String.format("\"%s\"", value.toString());
    }

    @Override
    protected Character getResultImpl(final ResultSet rset, final int index) throws SQLException {
        final String s = rset.getString(index);
        return (s != null ? s.charAt(0) : null);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Character value)
            throws SQLException {
        stmt.setString(index, value.toString());
    }
}
