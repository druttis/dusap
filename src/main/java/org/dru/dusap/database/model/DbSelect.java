package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DbSelect extends DbConditional {
    public static Builder field(final DbMember<?> field) {
        return new Builder().field(field);
    }

    public static Builder fields(final Collection<DbMember<?>> fields) {
        return new Builder().fields(fields);
    }

    public static Builder fields(final DbMember<?> first, final DbMember<?>... rest) {
        return new Builder().fields(first, rest);
    }

    public static Builder fields(final DbTable<?> table) {
        return new Builder().fields(table.getMembers());
    }

    public static Builder extend(final DbSelect select) {
        final Builder builder = new Builder();
        select.getFields().forEach(builder::addField);
        select.getConditions().forEach(condition -> builder.addCondition(condition.getField(), condition.getImage()));
        builder.limit = select.getLimit();
        builder.offset = select.getOffset();
        builder.forUpdate = select.isForUpdate();
        return builder;
    }

    private final Integer limit;
    private final Integer offset;
    private final boolean forUpdate;

    private DbSelect(final List<DbMember<?>> fields, final DbTable<?> table, final List<DbCondition<?>> conditions,
                     final Integer limit, final Integer offset, final boolean forUpdate) {
        super(fields, table, conditions);
        this.limit = limit;
        this.offset = offset;
        this.forUpdate = forUpdate;
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
        sb.append(getFields().stream()
                .flatMap(column -> column.getColumns().stream())
                .map(DbMember::getDbName)
                .collect(Collectors.joining(",")));
        sb.append(" FROM ");
        sb.append(getTable().getDbName());
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
        System.out.println(sb.toString());
        return sb.toString();
    }

    @Override
    protected int getFirstConditionIndex() {
        return 1;
    }

    public static class Builder extends DbConditional.Builder {
        private Integer limit;
        private Integer offset;
        private boolean forUpdate;

        private Builder() {
        }

        public Builder field(DbMember<?> field) {
            addField(field);
            return this;
        }

        public Builder fields(final Collection<DbMember<?>> fields) {
            Objects.requireNonNull(fields, "fields");
            fields.forEach(this::field);
            return this;
        }

        public Builder fields(final DbMember<?> first, final DbMember<?>... rest) {
            return fields(CollectionUtils.asList(first, rest));
        }

        public <T> Builder where(final DbMember<T> field, final String image) {
            addCondition(field, image);
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
            return new DbSelect(getFields(), getTable(), getConditions(), limit, offset, forUpdate);
        }
    }
}
