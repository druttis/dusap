package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public final class DbDelete extends DbConditional {
    public static Builder where(final DbColumn<?> column, final String image) {
        return new Builder().where(column, image);
    }

    private final DbTable<?> table;

    private DbDelete(final List<DbColumn<?>> columns, final List<DbCondition<?>> conditions, final DbTable<?> table) {
        super(columns, conditions);
        Objects.requireNonNull(table, "table");
        this.table = table;
    }

    public DbTable<?> getTable() {
        return table;
    }

    public <T> void setCondition(final PreparedStatement stmt, final DbColumn<T> column, final T value)
            throws SQLException {
        setCondition(stmt, column, 0, value);
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(table.getDbName());
        appendWhereSQL(sb);
        return sb.toString();
    }

    public static final class Builder {
        private final List<DbColumn<?>> columns;
        private final Set<DbTable<?>> tables;
        private final List<DbCondition<?>> conditions;

        private Builder() {
            columns = new ArrayList<>();
            tables = new HashSet<>();
            conditions = new ArrayList<>();
        }

        public Builder column(final DbColumn<?> column) {
            Objects.requireNonNull(column, "column");
            columns.add(column);
            addColumnTable(column);
            return this;
        }

        public Builder columns(final Collection<DbColumn<?>> columns) {
            Objects.requireNonNull(columns, "columns");
            columns.forEach(this::column);
            return this;
        }

        public Builder columns(final DbColumn<?> first, final DbColumn<?>... rest) {
            return columns(CollectionUtils.asList(first, rest));
        }

        public Builder where(final DbColumn<?> column, final String image) {
            conditions.add(new DbCondition<>(column, image));
            addColumnTable(column);
            return this;
        }

        public DbDelete build() {
            return new DbDelete(columns, conditions, tables.iterator().next());
        }

        private void addColumnTable(final DbColumn<?> column) {
            tables.add(column.getTable());
            if (tables.size() > 1) {
                throw new IllegalArgumentException("multiple tables not supported");
            }
        }
    }
}
