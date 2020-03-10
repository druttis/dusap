package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class DbSelect extends DbStatement {
    public static DbSelect count() {
        return new DbSelect(Collections.singletonList(new Count()));
    }

    public static DbSelect columns(final Collection<DbColumn<?>> columns) {
        return new DbSelect(columns.stream().map(Source::new).collect(Collectors.toList()));
    }

    public static DbSelect columns(final DbColumn<?> first, final DbColumn<?>... rest) {
        return columns(CollectionUtils.asList(first, rest));
    }

    public static DbSelect column(final DbColumn<?> column) {
        return columns(Collections.singleton(column));
    }

    private final Collection<Item> items;
    private final Map<DbColumn<?>, Integer> columnIndexByColumn;
    private int columnIndexCount;
    private Integer offset;
    private Integer limit;
    private boolean forUpdate;

    private DbSelect(final Collection<Item> items) {
        Objects.requireNonNull(items, "items");
        this.items = items;
        columnIndexByColumn = new HashMap<>();
        items.forEach(item -> item.consume(this::addColumnIndex));
    }

    @Override
    protected String getSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(items.stream().map(Item::getSQL).collect(Collectors.joining(",")));
        sb.append(" FROM ");
        sb.append(getTable().getDbName());
        sb.append(super.getSQL());
        if (limit != null) {
            sb.append(" LIMIT ");
            if (offset != null) {
                sb.append(offset);
            }
            sb.append(",");
            sb.append(limit);
        }
        if (forUpdate) {
            sb.append(" FOR UPDATE");
        }
        return sb.toString();
    }

    public DbSelect where(final DbColumn<?> column, final String op) {
        addCondition(column, op);
        return this;
    }

    public <C> DbSelect where(final DbColumn<C> column, final String op, final C value) {
        addCondition(column, op, value);
        return this;
    }

    public <C> DbSelect where(final DbColumn<C> column, final String op, final DbColumn<C> other) {
        addCondition(column, op, other);
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public DbSelect offset(final int offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public DbSelect limit(final int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public DbSelect forUpdate() {
        forUpdate = true;
        return this;
    }

    public <C> C getResult(final ResultSet rset, final DbColumn<C> column) throws SQLException {
        Objects.requireNonNull(column, "column");
        final Integer index = columnIndexByColumn.get(column);
        if (index == null) {
            throw new IllegalArgumentException("column has no index: column=" + column.getName());
        }
        return column.getResult(rset, index);
    }

    private void addColumnIndex(final DbColumn<?> column) {
        Objects.requireNonNull(column, "column");
        columnIndexByColumn.compute(column, ($, index) -> {
            if (index != null) {
                throw new IllegalArgumentException("column index already exist: column=" + column.getName());
            }
            return ++columnIndexCount;
        });
    }

    private static abstract class Item {
        protected Item() {
        }

        protected abstract void consume(final Consumer<DbColumn<?>> consumer);

        protected abstract String getSQL();
    }

    private static final class Count extends Item {
        private Count() {
        }

        @Override
        protected void consume(final Consumer<DbColumn<?>> consumer) {
        }

        @Override
        protected String getSQL() {
            return "COUNT(*)";
        }
    }

    private static final class Source extends Item {
        private final DbColumn<?> column;

        private Source(final DbColumn<?> column) {
            Objects.requireNonNull(column, "column");
            this.column = column;
        }

        @Override
        protected void consume(final Consumer<DbColumn<?>> consumer) {
            consumer.accept(column);
        }

        @Override
        protected String getSQL() {
            return column.getDbName();
        }
    }
}
