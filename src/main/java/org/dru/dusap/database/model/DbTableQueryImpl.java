package org.dru.dusap.database.model;

import org.dru.dusap.util.Tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class DbTableQueryImpl<T> implements DbTableQuery<T> {
    private final DbTable<T> table;
    private final String primaryKeyConditions;
    private final String allColumnNames;
    private final String questionMarks;
    private final String assignNonPrimaryKeyColumns;

    public DbTableQueryImpl(final DbTable<T> table) {
        this.table = Objects.requireNonNull(table, "table");
        primaryKeyConditions = table.getPrimaryKeyColumns().stream()
                .map(c -> String.format("%s=?", c.getDbName()))
                .collect(Collectors.joining());
        allColumnNames = table.getColumns().stream()
                .map(DbColumn::getDbName)
                .collect(Collectors.joining(","));
        questionMarks = table.getColumns().stream()
                .map(c -> "?")
                .collect(Collectors.joining(","));
        assignNonPrimaryKeyColumns = table.getNonPrimaryKeyColumns().stream()
                .map(c -> String.format("%s=VALUES(%s)", c.getDbName(), c.getDbName()))
                .collect(Collectors.joining(","));
    }

    @Override
    public T getOne(final Connection conn, final Object... primaryKeys) throws SQLException {
        final List<T> objects = getImpl(conn, true, false, primaryKeyConditions, primaryKeys);
        return (objects.isEmpty() ? null : objects.get(0));
    }

    @Override
    public List<T> getAll(final Connection conn, final String conditions, final Object... params) throws SQLException {
        return getImpl(conn, false, false, conditions, params);
    }

    @Override
    public List<T> getAll(final Connection conn, final int limit, final int offset) throws SQLException {
        if (limit < 1) {
            throw new IllegalArgumentException("limit has to be 1 or greater: " + limit);
        } else if (offset < 0) {
            throw new IllegalArgumentException("negative offset: " + offset);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("LIMIT ").append(limit);
        if (offset > 0) {
            sb.append(" OFFSET ").append(offset);
        }
        return getImpl(conn, false, false, sb.toString());
    }

    @Override
    public List<T> getAll(final Connection conn, final int limit) throws SQLException {
        return getAll(conn, limit, 0);
    }

    @Override
    public List<T> getAll(final Connection conn) throws SQLException {
        return getImpl(conn, false, false, null);
    }

    @Override
    public void insertOne(final Connection conn, final T object) throws SQLException {
        insertAll(conn, Collections.singletonList(object));
    }

    @Override
    public void insertAll(final Connection conn, final Collection<T> objects) throws SQLException {
        try (final PreparedStatement stmt = conn.prepareStatement(getInsertSql())) {
            for (final T object : objects) {

            }
        }
    }

    @Override
    public void setOne(final Connection conn, final T object) throws SQLException {

    }

    @Override
    public void setAll(final Connection conn, final Collection<T> objects) throws SQLException {

    }

    @Override
    public Tuple<T, T> updateOne(final Connection conn, final UnaryOperator<T> operator, final String conditions,
                                 final Object... params) throws SQLException {
        return null;
    }

    @Override
    public List<Tuple<T, T>> updateAll(final Connection conn, final UnaryOperator<T> operator, final String conditions,
                                       final Object... params) throws SQLException {
        return null;
    }

    @Override
    public void replaceOne(final Connection conn, final Tuple<T, T> tuple) throws SQLException {

    }

    @Override
    public void replaceAll(final Connection conn, final List<Tuple<T, T>> tuples) throws SQLException {

    }

    @Override
    public void deleteOne(final Connection conn, final Object... primaryKeys) throws SQLException {
    }

    @Override
    public void deleteAll(final Connection conn, final String conditions, final Object... params) throws SQLException {

    }

    @Override
    public void removeOne(final Connection conn, final T object) throws SQLException {

    }

    @Override
    public void removeAll(final Connection conn, final Collection<T> objects) throws SQLException {

    }

    private List<T> getImpl(final Connection conn, final boolean single, final boolean locked,
                            final String conditions, final Object... params) throws SQLException {
        Objects.requireNonNull(conn, "conn");
        Objects.requireNonNull(params, "params");
        final StringBuilder sb = new StringBuilder("SELECT ").append(allColumnNames);
        sb.append(" FROM ").append(table.getDbName());
        if (conditions != null) {
            sb.append(" WHERE ").append(conditions);
        } else if (params.length != 0) {
            throw new IllegalArgumentException("no conditions, but params");
        }
        if (single) {
            sb.append(" LIMIT 2");
        }
        if (locked) {
            sb.append(" FOR UPDATE");
        }
        try (final PreparedStatement stmt = conn.prepareStatement(sb.toString())) {
            setParams(stmt, 1, params);
            if (single) {
                stmt.setMaxRows(2);
            }
            try (final ResultSet rset = stmt.executeQuery()) {
                final List<T> objects = new ArrayList<>();
                while (rset.next()) {
                    objects.add(table.getResult(rset));
                }
                if (single && objects.size() > 1) {
                    throw new SQLException("multiple matches for query '" + sb.toString() + "'");
                }
                return objects;
            }
        }
    }

    private String getInsertSql() {
        return String.format("INSERT INTO %s (%s) VALUES (%s)", table.getDbName(), allColumnNames, questionMarks);
    }

    private String getSetSql() {
        return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertSql(), assignNonPrimaryKeyColumns);
    }

    private void setParams(final PreparedStatement stmt, final int index, final Object... params) throws SQLException {
        int localIndex = index;
        for (final Object param : params) {
            stmt.setObject(localIndex, param);
            localIndex++;
        }
    }
}
