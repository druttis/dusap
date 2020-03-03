package org.dru.dusap.database.model;

import org.dru.dusap.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DbInsert extends DbCommand {
    public static Builder field(final DbMember<?> field) {
        return new Builder().field(field);
    }

    public static Builder fields(final Collection<DbMember<?>> fields) {
        return new Builder().fields(fields);
    }

    public static Builder fields(final DbMember<?> first, final DbMember<?>... rest) {
        return new Builder().fields(first, rest);
    }

    private DbInsert(final List<DbMember<?>> fields, final DbTable<?> table) {
        super(fields, table);
    }

    @Override
    protected String createSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(getTable().getDbName());
        sb.append(" (");
        sb.append(getFields().stream()
                .flatMap(field -> field.getColumns().stream())
                .map(DbMember::getDbName)
                .collect(Collectors.joining(","))
        );
        sb.append(") VALUES (");
        sb.append(getFields().stream()
                .flatMap(field -> field.getColumns().stream())
                .map($ -> "?")
                .collect(Collectors.joining(","))
        );
        sb.append(")");
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static final class Builder extends DbCommand.Builder {
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

        public DbInsert build() {
            return new DbInsert(getFields(), getTable());
        }
    }
}
