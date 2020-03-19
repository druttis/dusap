package org.dru.dusap.database.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DbStatement a convenient class to deal with ResultSet and PreparedStatement
 * via DbTable and DbColumn
 * <p/>
 * A DbStatement is created by the method parse(sql, ...args);
 * <p/>
 * %c - Add COUNT(*) to the sql, and set countIndex to be used with DbStatement.getCount()<br/>
 * %d - Expect next argument to be an Integer, add it to the sql<br/>
 * %r - Expect next argument to be a DbColumn, add its name to the sql, and add it to the result index list-map<br/>
 * %n - Expect next argument to be an DbEntity and add its name to the sql<br/>
 * %p - Expect next argument to be a DbColumn, add its name to the sql, and add it to the parameter index list-map<br/>
 * %l - Expect previous argument to be a DbColumn, add ? to the sql, and add it to the parameter index list-map<br/>
 * %L - Expect previous argument to be a DbColumn and next Argument to be an Integer, add ?'s to the sql and add it to the parameter index list-map that many number of times<br/>
 * %&lt;N&gt; - Expect N argument to be a DbColumn, add its name to the sql<br/>
 * <p/>
 * Example:<br/>
 * <code>SELECT %c FROM %n WHERE %p&lt;?</code>
 * with args: dbTable, dbAge
 * <p/>
 * DbStatement selectCount = DbStatement.parse("SELECT %c FROM %n", dbTable);
 * PreparedStatement stmt = selectCount.prepareStatement();
 * ResultSet rset = stmt.executeQuery();
 * rset.next();
 * System.out.println(selectCount.getCount(rset));
 */

public final class DbStatement {
    public static DbStatement parse(final String sql, final Object... args) {
        return parse(sql, Arrays.asList(args));
    }

    private static DbStatement parse(final String sql, final List<?> args) {
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(args, "args");
        final Map<DbColumn<?>, List<Integer>> rsetIndexesByDbColumn = new HashMap<>();
        int rsetIndexCounter = 0;
        final Map<DbColumn<?>, List<Integer>> stmtIndexesByDbColumn = new HashMap<>();
        int stmtIndexCounter = 0;
        int index = 0;
        int countIndex = -1;
        final StringBuilder sb = new StringBuilder();
        final Iterator<?> it = args.iterator();
        DbColumn<?> last = null;
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
                if (ch == 'c') {
                    sb.append("COUNT(*)");
                    countIndex = ++rsetIndexCounter;
                } else if (ch == 'd') {
                    final int value = Integer.parseInt(it.next().toString());
                    sb.append(value);
                } else if (ch == 'l') {
                    if (last == null) {
                        throw new IllegalStateException("no previous parameter");
                    }
                    sb.append("?");
                    rsetIndexesByDbColumn.computeIfAbsent(last, $ -> new ArrayList<>()).add(++rsetIndexCounter);
                } else if (ch == 'L') {
                    if (last == null) {
                        throw new IllegalStateException("no previous parameter");
                    }
                    final int amount = Integer.parseInt(it.next().toString());
                    sb.append(IntStream.range(0, amount).mapToObj(i -> "?").collect(Collectors.joining(",")));
                    for (int count = 0; count < amount; count++) {
                        rsetIndexesByDbColumn.computeIfAbsent(last, $ -> new ArrayList<>()).add(++rsetIndexCounter);
                    }
                } else if (ch == 'n') {
                    final DbEntity<?> entity = (DbEntity<?>) it.next();
                    if (entity instanceof DbColumn<?>) {
                        last = (DbColumn<?>) entity;
                    }
                    sb.append(entity.getDbName());
                } else if (ch == 'p') {
                    final DbColumn<?> column = (DbColumn<?>) it.next();
                    last = column;
                    sb.append(column.getDbName());
                    stmtIndexesByDbColumn.computeIfAbsent(column, $ -> new ArrayList<>()).add(++stmtIndexCounter);
                } else if (ch == 'r') {
                    final DbColumn<?> column = (DbColumn<?>) it.next();
                    last = column;
                    sb.append(column.getDbName());
                    rsetIndexesByDbColumn.computeIfAbsent(column, $ -> new ArrayList<>()).add(++rsetIndexCounter);
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
                    final DbColumn<?> column = (DbColumn<?>) args.get(num);
                    last = column;
                    sb.append(column.getDbName());
                }
            } else {
                sb.append(ch);
            }
        }
        return new DbStatement(sql, args, sb.toString(), rsetIndexesByDbColumn, stmtIndexesByDbColumn, countIndex);
    }

    private final String raw;
    private final List<?> args;
    private final String sql;
    private final Map<DbColumn<?>, List<Integer>> rsetIndexesByColumn;
    private final Map<DbColumn<?>, List<Integer>> stmtIndexesByColumn;
    private final int countIndex;

    private DbStatement(final String raw, final List<?> args, final String sql,
                        final Map<DbColumn<?>, List<Integer>> rsetIndexesByColumn,
                        final Map<DbColumn<?>, List<Integer>> stmtIndexesByColumn, final int countIndex) {
        this.raw = raw;
        this.args = args;
        this.sql = sql;
        this.rsetIndexesByColumn = rsetIndexesByColumn;
        this.stmtIndexesByColumn = stmtIndexesByColumn;
        this.countIndex = countIndex;
    }

    public String getRaw() {
        return raw;
    }

    public String getSQL() {
        return sql;
    }

    public DbStatement extend(final String sql, final Object... args) {
        return extend(sql, Arrays.asList(args));
    }

    public DbStatement extend(final String sql, final List<?> args) {
        final List<Object> list = new ArrayList<>(this.args);
        list.addAll(args);
        return parse(raw + " " + sql, list);
    }

    public PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(sql);
    }

    public int getCount(final ResultSet rset) throws SQLException {
        if (countIndex == -1) {
            throw new IllegalArgumentException("SQL statement does not have %c (for count(*)): sql=" + getRaw());
        }
        return rset.getInt(countIndex);
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
        column.setParameter(stmt, getStmtIndexes(column).get(index), value);
    }

    public <C> void setParameter(final PreparedStatement stmt, final DbColumn<C> column, final C value)
            throws SQLException {
        setParameter(stmt, column, 0, value);
    }

    public <C> void setParameters(final PreparedStatement stmt, final DbColumn<C> column, final int index,
                                  final Collection<C> values) throws SQLException {
        final List<Integer> stmtIndexes = getStmtIndexes(column);
        int local = index;
        for (final C value : values) {
            column.setParameter(stmt, stmtIndexes.get(local++), value);
        }
    }

    public <C> void setParameters(final PreparedStatement stmt, final DbColumn<C> column, final Collection<C> values)
            throws SQLException {
        setParameters(stmt, column, 0, values);
    }

    private List<Integer> getStmtIndexes(final DbColumn<?> column) {
        Objects.requireNonNull(column, "column");
        final List<Integer> indexes = stmtIndexesByColumn.get(column);
        if (indexes == null) {
            throw new IllegalArgumentException(sql + ": no prepared-statement indexes for column: name="
                    + column.getName());
        }
        return indexes;
    }
}
