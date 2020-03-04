package org.dru.dusap.database.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DbConditional extends DbCommand {
    private final List<DbCondition<?>> conditions;
    private final Map<DbMember<?>, Integer> indexByCondition;

    protected DbConditional(final List<DbMember<?>> fields, final DbTable<?> table,
                            final List<DbCondition<?>> conditions) {
        super(fields, table);
        Objects.requireNonNull(conditions, "conditions");
        this.conditions = Collections.unmodifiableList(new ArrayList<>(conditions));
        indexByCondition = new HashMap<>();
        int index = getFirstConditionIndex();
        for (final DbCondition<?> condition : conditions) {
            final DbMember<?> field = condition.getField();
            indexByCondition.put(field, index);
            index += field.getColumnCount();
        }
    }

    public final List<DbCondition<?>> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public final int getConditionIndex(final DbMember<?> field) {
        Objects.requireNonNull(field, "field");
        final int index = indexByCondition.getOrDefault(field, -1);
        if (index == -1) {
            throw new IllegalArgumentException("no condition for field: name=" + field.getName());
        }
        return index;
    }

    public final <T> void setCondition(final PreparedStatement stmt, final DbMember<T> field, final T value)
            throws SQLException {
        field.setParameter(stmt, getConditionIndex(field), value);
    }

    protected final void appendWhereSQL(final StringBuilder sb) {
        if (!conditions.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(conditions.stream().map(DbCondition::getSQL).collect(Collectors.joining(" AND ")));
        }
    }

    protected abstract int getFirstConditionIndex();

    public static abstract class Builder extends DbCommand.Builder {
        private final List<DbCondition<?>> conditions;

        protected Builder() {
            conditions = new ArrayList<>();
        }

        protected final List<DbCondition<?>> getConditions() {
            return Collections.unmodifiableList(conditions);
        }

        protected final void addCondition(final DbMember<?> field, final String image) {
            final DbCondition<?> condition = new DbCondition<>(field, image);
            for (final DbCondition<?> c : conditions) {
                if (c.getField().getName().equals(field.getName())) {
                    throw new IllegalArgumentException("duplicate condition: name=" + field.getName());
                }
            }
            conditions.add(condition);
            addFieldTable(field);
        }
    }
}
