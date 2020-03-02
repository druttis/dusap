package org.dru.dusap.database.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class DbCommand {
    private final List<DbColumn<?>> columns;
    private final Map<DbColumn<?>, Integer> columnIndexes;
    protected String sql;

    public DbCommand(final List<DbColumn<?>> columns) {
        Objects.requireNonNull(columns, "columns");
        this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
        columnIndexes = new HashMap<>();
        columns.forEach(c -> columnIndexes.put(c, columns.indexOf(c)));
    }

    public final List<DbColumn<?>> getColumns() {
        return columns;
    }

    public final int getColumnIndex(final DbColumn<?> column) {
        final int index = columnIndexes.getOrDefault(column, -1);
        if (index == -1) {
            throw new IllegalArgumentException("no such column: name=" + column);
        }
        return index;
    }

    public final String getSQL() {
        if (sql == null) {
            sql = createSQL();
        }
        return sql;
    }

    public final PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(getSQL());
    }

    public final <T> T getColumn(final ResultSet rset, final DbColumn<T> column) throws SQLException {
        return column.getResult(rset, columnIndexes.get(column) + 1);
    }

    public final <T> void setColumn(final PreparedStatement stmt, final DbColumn<T> column, final T value)
            throws SQLException {
        column.setParameter(stmt, columnIndexes.get(column) + 1, value);
    }

    protected abstract String createSQL();
}
