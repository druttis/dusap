package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class DbInsert extends DbCommand {
    public static Builder column(final DbColumn<?> column) {
        return new Builder().column(column);
    }

    public static Builder columns(final Collection<DbColumn<?>> columns) {
        return new Builder().columns(columns);
    }

    public static Builder columns(final DbColumn<?> first, final DbColumn<?>... rest) {
        return new Builder().columns(first, rest);
    }

    private final DbTable<?> table;

    private DbInsert(final List<DbColumn<?>> columns, final DbTable<?> table) {
        super(columns);
        Objects.requireNonNull(table, "table");
        this.table = table;
    }

    public DbTable<?> getTable() {
        return table;
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(getTable().getDbName());
        sb.append(" (");
        sb.append(getColumns().stream().map(DbColumn::getDbName).collect(Collectors.joining(",")));
        sb.append(") VALUES (");
        sb.append(getColumns().stream().map($ -> "?").collect(Collectors.joining(",")));
        sb.append(")");
        return sb.toString();
    }

    public static final class Builder {
        private final List<DbColumn<?>> columns;
        private final Set<DbTable<?>> tables;

        public Builder() {
            columns = new ArrayList<>();
            tables = new HashSet<>();
        }

        public Builder column(DbColumn<?> column) {
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

        public DbInsert build() {
            return new DbInsert(columns, tables.iterator().next());
        }

        private void addColumnTable(final DbColumn<?> column) {
            tables.add(column.getTable());
            if (tables.size() > 1) {
                throw new IllegalArgumentException("multiple tables not supported");
            }
        }

    }
}
