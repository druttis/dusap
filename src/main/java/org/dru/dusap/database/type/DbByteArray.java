package org.dru.dusap.database.type;

import org.dru.dusap.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DbByteArray extends AbstractDbType<byte[]> {
    public static final DbByteArray INSTANCE = new DbByteArray();

    private DbByteArray() {
        super(JDBCType.BLOB, false, true);
    }

    @Override
    protected String getSQLImpl(final byte[] value) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : value) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return String.format("UNHEX('%s')", sb.toString());
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
