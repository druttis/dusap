package org.dru.dusap.database.model;

import java.util.*;
import java.util.stream.Collectors;

public final class DbSelect {
    private final List<DbMember<?>> fields;
    private final Set<DbTable<?>> tables;
    private final List<DbCondition<?>> conditions;

    public DbSelect() {
        fields = new ArrayList<>();
        tables = new HashSet<>();
        conditions = new ArrayList<>();
    }


    public DbSelect field(final DbMember<?> field) {
        return fields(Collections.singleton(field));
    }

    public DbSelect fields(final DbMember<?>... fields) {
        return fields(Arrays.asList(fields));
    }

    public DbSelect fields(final Collection<DbMember<?>> fields) {
        this.fields.addAll(fields);
        tables.addAll(fields.stream().map(DbMember::getTable).collect(Collectors.toList()));
        return this;
    }

    public DbSelect where(final DbMember<?> field, String rest) {
        conditions.add(new DbCondition<>(field, rest));
        tables.add(field.getTable());
        return this;
    }

    public String getSQL() {
        final StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(fields.stream().map(DbMember::getFullyQualifiedDbName).collect(Collectors.joining(",")));
        sb.append(" FROM ");
        sb.append(tables.stream().map(DbTable::getDbName).collect(Collectors.joining(",")));
        if (!conditions.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(conditions.stream().map(DbCondition::getSQL).collect(Collectors.joining(" AND ")));
        }
        return sb.toString();
    }

    private static class DbCondition<T> {
        private final DbMember<T> field;
        private final String rest;

        private DbCondition(final DbMember<T> field, final String rest) {
            this.field = field;
            this.rest = rest;
        }

        public DbMember<T> getField() {
            return field;
        }

        public String getRest() {
            return rest;
        }

        public String getSQL() {
            return field.getFullyQualifiedDbName() + " " + rest;
        }
    }
}
