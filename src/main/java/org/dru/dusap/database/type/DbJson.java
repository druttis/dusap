package org.dru.dusap.database.type;

import org.dru.dusap.io.OutputInputStream;
import org.dru.dusap.json.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class DbJson<T> extends AbstractDbType<T> {
    public static <T> DbJson<T> instance(final Class<T> type, final JsonSerializer jsonSerializer) {
        return new DbJson<>(type, jsonSerializer);
    }

    private final Class<T> type;
    private final JsonSerializer jsonSerializer;

    private DbJson(final Class<T> type, final JsonSerializer jsonSerializer) {
        super(JDBCType.BLOB, true);
        this.type = Objects.requireNonNull(type, "type");
        this.jsonSerializer = Objects.requireNonNull(jsonSerializer, "jsonSerializer");
    }

    @Override
    protected T getResultImpl(final ResultSet rset, final int index) throws SQLException {
        final InputStream in = rset.getBinaryStream(index);
        if (in == null) {
            return null;
        }
        try {
            return jsonSerializer.readObject(in, type);
        } catch (final IOException exc) {
            throw new SQLException("failed to read json", exc);
        }
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final T value)
            throws SQLException {
        final OutputInputStream out = new OutputInputStream();
        try {
            jsonSerializer.writeObject(out, value);
        } catch (final IOException exc) {
            throw new SQLException("failed to write json", exc);
        }
        stmt.setBinaryStream(index, out.getInputStream());
    }
}
