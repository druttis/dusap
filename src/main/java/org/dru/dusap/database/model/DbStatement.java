package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class DbStatement {
    public static DbStatement parse(final String sql, final Object first, final Object... rest) {
        return parse(sql, CollectionUtils.asList(first, rest));
    }

    public static DbStatement parse(final String sql, final List<?> args) {
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(args, "args");
        final StringBuilder sb = new StringBuilder();
        final Iterator<?> it = args.iterator();
        final Map<DbColumn<?>, List<Integer>> rsetIndexesByDbColumn = new HashMap<>();
        final Map<DbColumn<?>, List<Integer>> stmtIndexesByDbColumn = new HashMap<>();
        int rsetIndexCounter = 0;
        int stmtIndexCounter = 0;
        int index = 0;

        while (index < sql.length()) {
            char ch = sql.charAt(index++);
            if (ch == '"' || ch == '\'') {
                char ec = ch;
                if (index == sql.length()) {
                    throw new IllegalArgumentException("unexpected end of line in quotation");
                }
                ch = sql.charAt(index++);
                while (ch != ec) {
                    sb.append(ch);
                    if (index == sql.length()) {
                        throw new IllegalArgumentException("unexpected end of line in quotation");
                    }
                    ch = sql.charAt(index++);
                }
            } else if (ch == '%') {
                if (index == sql.length()) {
                    throw new IllegalArgumentException("unexpected end of line after %");
                }
                ch = sql.charAt(index++);
                if (ch == 'r') {
                    final DbColumn<?> column = (DbColumn<?>) it.next();
                    sb.append(column.getDbName());
                    rsetIndexesByDbColumn.computeIfAbsent(column, $ -> new ArrayList<>()).add(++rsetIndexCounter);
                } else if (ch == 'n') {
                    final DbEntity<?> entity = (DbEntity<?>) it.next();
                    sb.append(entity.getDbName());
                } else if (ch == 'p') {
                    final DbColumn<?> column = (DbColumn<?>) it.next();
                    sb.append(column.getDbName());
                    stmtIndexesByDbColumn.computeIfAbsent(column, $ -> new ArrayList<>()).add(++stmtIndexCounter);
                } else if (ch >= '0' && ch <= '9') {
                    int num = ch - '0';
                    while (index < sql.length()) {
                        ch = sql.charAt(index++);
                        if (ch < '0' || ch >= '9') {
                            index--;
                            break;
                        }
                        num = (num * 10) + ch;
                    }
                    final DbEntity<?> entity = (DbEntity<?>) args.get(num);
                    sb.append(entity.getDbName());
                }
            } else {
                sb.append(ch);
            }
        }
        return new DbStatement(sb.toString(), rsetIndexesByDbColumn, stmtIndexesByDbColumn);
    }

    private final String sql;
    private final Map<DbColumn<?>, List<Integer>> rsetIndexesByColumn;
    private final Map<DbColumn<?>, List<Integer>> stmtIndexesByColumn;

    private DbStatement(final String sql, final Map<DbColumn<?>, List<Integer>> rsetIndexesByColumn,
                        final Map<DbColumn<?>, List<Integer>> stmtIndexesByColumn) {
        this.sql = sql;
        this.rsetIndexesByColumn = rsetIndexesByColumn;
        this.stmtIndexesByColumn = stmtIndexesByColumn;
        System.out.println(this);
    }

    public PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(sql);
    }

    public <C> C getResult(final ResultSet rset, final DbColumn<C> column, final int index) throws SQLException {
        final List<Integer> indexes = rsetIndexesByColumn.get(column);
        if (indexes == null) {
            throw new IllegalArgumentException(sql + ": no result-set indexes for column: name=" + column.getName());
        }
        return column.getResult(rset, indexes.get(index));
    }

    public <C> C getResult(final ResultSet rset, final DbColumn<C> column) throws SQLException {
        return getResult(rset, column, 0);
    }

    public <C> void setParameter(final PreparedStatement stmt, final DbColumn<C> column, final int index,
                                 final C value) throws SQLException {
        final List<Integer> indexes = stmtIndexesByColumn.get(column);
        if (indexes == null) {
            throw new IllegalArgumentException(sql + ": no prepared-statement indexes for column: name=" + column.getName());
        }
        column.setParameter(stmt, indexes.get(index), value);
    }

    public <C> void setParameter(final PreparedStatement stmt, final DbColumn<C> column, final C value)
            throws SQLException {
        setParameter(stmt, column, 0, value);
    }

    @Override
    public String toString() {
        return "DbStatement{" +
                "sql='" + sql + '\'' +
                ", rsetIndexesByColumn=" + rsetIndexesByColumn +
                ", stmtIndexesByColumn=" + stmtIndexesByColumn +
                '}';
    }
}
