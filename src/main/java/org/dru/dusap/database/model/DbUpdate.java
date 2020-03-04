package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DbUpdate extends DbConditional {
    public static Builder field(final DbMember<?> field) {
        return new Builder().field(field);
    }

    public static Builder fields(final Collection<DbMember<?>> fields) {
        return new Builder().fields(fields);
    }

    public static Builder fields(final DbMember<?> first, final DbMember<?>... rest) {
        return new Builder().fields(first, rest);
    }

    private DbUpdate(final List<DbMember<?>> fields, final DbTable<?> table, final List<DbCondition<?>> conditions) {
        super(fields, table, conditions);
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(getTable().getDbName());
        sb.append(" SET ");
        sb.append(getFields().stream()
                .flatMap(field -> field.getColumns().stream())
                .map(column -> String.format("%s%s", column.getDbName(), "=?"))
                .collect(Collectors.joining(",")));
        appendWhereSQL(sb);
        System.out.println(sb.toString());
        return sb.toString();
    }

    @Override
    protected int getFirstConditionIndex() {
        return getNextIndex();
    }

    public static final class Builder extends DbConditional.Builder {
        private Builder() {
        }

        public Builder field(final DbMember<?> field) {
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

        public Builder where(final DbMember<?> field, final String image) {
            addCondition(field, image);
            return this;
        }

        public DbUpdate build() {
            return new DbUpdate(getFields(), getTable(), getConditions());
        }
    }
}
