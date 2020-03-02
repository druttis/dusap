package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public final class DbSelect extends DbConditional {
    public static Builder column(DbColumn<?> column) {
        final Builder builder = new Builder();
        return builder.column(column);
    }

    public static Builder column(Collection<DbColumn<?>> columns) {
        final Builder builder = new Builder();
        return builder.columns(columns);
    }

    public static Builder column(final DbColumn<?> first, final DbColumn<?>... rest) {
        final Builder builder = new Builder();
        return builder.columns(first, rest);
    }

    public static Builder columns(final DbTable<?> table) {
        final Builder builder = new Builder();
        return builder.columns(table.getDbColumns());
    }

    public static Builder extend(final DbSelect select) {
        final Builder builder = new Builder();
        builder.columns.addAll(select.getColumns());
        builder.tables.addAll(select.getTables());
        builder.conditions.addAll(select.getConditions());
        builder.limit = select.getLimit();
        builder.offset = select.getOffset();
        builder.forUpdate = select.isForUpdate();
        return builder;
    }

    private final List<DbTable<?>> tables;
    private final Integer limit;
    private final Integer offset;
    private final boolean forUpdate;

    public DbSelect(final List<DbColumn<?>> columns, final List<DbCondition<?>> conditions,
                    final Set<DbTable<?>> tables, final Integer limit, final Integer offset, final boolean forUpdate) {
        super(columns, conditions);
        this.tables = Collections.unmodifiableList(new ArrayList<>(tables));
        this.limit = limit;
        this.offset = offset;
        this.forUpdate = forUpdate;
    }

    public List<DbTable<?>> getTables() {
        return tables;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(getColumns().stream().map(DbColumn::getDbName).collect(Collectors.joining(",")));
        sb.append(" FROM ");
        sb.append(tables.stream().map(DbTable::getDbName).collect(Collectors.joining(",")));
        appendWhereSQL(sb);
        if (limit != null) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        if (offset != null) {
            sb.append(" OFFSET ");
            sb.append(offset);
        }
        if (forUpdate) {
            sb.append(" FOR UPDATE");
        }
        return sb.toString();
    }

    public <T> void setCondition(final PreparedStatement stmt, final DbColumn<T> column, final T value)
            throws SQLException {
        setCondition(stmt, column, 0, value);
    }

    public static class Builder {
        private final List<DbColumn<?>> columns;
        private final Set<DbTable<?>> tables;
        private final List<DbCondition<?>> conditions;
        private Integer limit;
        private Integer offset;
        private boolean forUpdate;

        private Builder() {
            columns = new ArrayList<>();
            tables = new LinkedHashSet<>();
            conditions = new ArrayList<>();
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

        public <T> Builder where(final DbColumn<T> column, final String image) {
            conditions.add(new DbCondition<>(column, image));
            return this;
        }

        public <T> Builder limit(final int limit) {
            this.limit = limit;
            return this;
        }

        public <T> Builder offset(final int offset) {
            this.offset = offset;
            return this;
        }

        public <T> Builder forUpdate() {
            forUpdate = true;
            return this;
        }

        public DbSelect build() {
            return new DbSelect(columns, conditions, tables, limit, offset, forUpdate);
        }

        private void addColumnTable(final DbColumn<?> column) {
            tables.add(column.getTable());
        }
    }
}
