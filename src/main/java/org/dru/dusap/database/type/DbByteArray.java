package org.dru.dusap.database.type;

import org.dru.dusap.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbByteArray extends AbstractDbType<byte[]> {
    public static final DbByteArray INSTANCE = new DbByteArray();

    private DbByteArray() {
        super(JDBCType.BLOB, true);
    }

    @Override
    protected byte[] getResultImpl(final ResultSet rset, final int index) throws SQLException {
        final InputStream in = rset.getBinaryStream(index);
        if (in == null) {
            return null;
        }
        try {
            return IOUtils.readBytes(in);
        } catch (final IOException exc) {
            throw new SQLException("failed to ready bytes", exc);
        }
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final byte[] value)
            throws SQLException {
        stmt.setBinaryStream(index, new ByteArrayInputStream(value));
    }
}
