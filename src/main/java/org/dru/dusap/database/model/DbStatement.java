package org.dru.dusap.database.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DbStatement {
    private final Map<DbColumn<?>, List<Integer>> parameterIndexesByColumn;
    private final List<Condition<?>> conditions;
    private int parameterIndexCount;
    private DbTable<?> table;

    protected DbStatement() {
        parameterIndexesByColumn = new HashMap<>();
        conditions = new ArrayList<>();
    }

    protected final void addParameterIndex(final DbColumn<?> column) {
        setTable(column);
        parameterIndexesByColumn.computeIfAbsent(column, $ -> new ArrayList<>()).add(++parameterIndexCount);
    }

    protected final void addCondition(final DbColumn<?> column, final String op) {
        addParameterIndex(column);
        conditions.add(new Parameter<>(column, op));
    }

    protected final <C> void addCondition(final DbColumn<C> column, final String op, final C value) {
        setTable(column);
        conditions.add(new Literal<>(column, op, value));
    }

    protected final <C> void addCondition(final DbColumn<C> column, final String op, final DbColumn<C> other) {
        setTable(column);
        setTable(other);
        conditions.add(new Reference<>(column, op, other));
    }

    public final PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(getSQL());
    }

    public final <C> void setParameter(final PreparedStatement stmt, final DbColumn<C> column, final int index,
                                       final C value) throws SQLException {
        Objects.requireNonNull(column, "column");
        if (column.getTable() != table) {
            throw new IllegalArgumentException("not a column of this statements table: column=" + column.getName());
        }
        final List<Integer> parameterIndexes = parameterIndexesByColumn.get(column);
        if (parameterIndexes == null) {
            throw new IllegalArgumentException("no indexes exist: column=" + column.getDbName());
        }
        final int parameterIndex = parameterIndexes.get(index);
        column.setParameter(stmt, parameterIndex, value);
    }

    public final <C> void setParameter(final PreparedStatement stmt, final DbColumn<C> column, final C value)
            throws SQLException {
        setParameter(stmt, column, 0, value);
    }

    protected String getSQL() {
        if (conditions.isEmpty()) {
            return "";
        } else {
            return " WHERE " + conditions.stream().map(Condition::getSQL).collect(Collectors.joining(" AND "));
        }
    }

    protected final DbTable<?> getTable() {
        return table;
    }

    protected final void setTable(final DbTable<?> table) {
        Objects.requireNonNull(table, "table");
        if (this.table == null) {
            this.table = table;
        } else if (table != this.table) {
            throw new UnsupportedOperationException("multiple tables not supported");
        }
    }

    protected final void setTable(final DbColumn<?> column) {
        setTable(column.getTable());
    }

    private static abstract class Condition<C> {
        protected final DbColumn<C> column;
        protected final String op;

        protected Condition(final DbColumn<C> column, final String op) {
            Objects.requireNonNull(column, "column");
            Objects.requireNonNull(op, "op");
            this.column = column;
            this.op = op;
        }

        protected abstract String getSQL();
    }

    private static final class Parameter<C> extends Condition<C> {
        private Parameter(final DbColumn<C> column, final String op) {
            super(column, op);
        }

        @Override
        protected String getSQL() {
            return column.getDbName() + op + "?";
        }
    }

    private static final class Literal<C> extends Condition<C> {
        private final C value;

        public Literal(final DbColumn<C> column, final String op, final C value) {
            super(column, op);
            this.value = value;
        }

        @Override
        protected String getSQL() {
            return column.getDbName() + op + column.getSQL(value);
        }
    }

    private static final class Reference<C> extends Condition<C> {
        private final DbColumn<C> value;

        private Reference(final DbColumn<C> column, final String op, final DbColumn<C> value) {
            super(column, op);
            Objects.requireNonNull(value, "value");
            this.value = value;
        }

        @Override
        protected String getSQL() {
            return column.getDbName() + op + value.getDbName();
        }
    }
}
