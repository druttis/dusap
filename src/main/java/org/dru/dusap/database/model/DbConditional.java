package org.dru.dusap.database.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DbConditional extends DbCommand {
    private final List<DbCondition<?>> conditions;
    private final Map<DbColumn<?>, Integer> conditionIndexes;

    public DbConditional(final List<DbColumn<?>> columns, final List<DbCondition<?>> conditions) {
        super(columns);
        Objects.requireNonNull(conditions, "conditions");
        this.conditions = Collections.unmodifiableList(new ArrayList<>(conditions));
        conditionIndexes = new HashMap<>();
        conditions.forEach(c -> conditionIndexes.put(c.getColumn(), conditions.indexOf(c)));
    }

    public final List<DbCondition<?>> getConditions() {
        return conditions;
    }

    public final int getConditionIndex(final DbColumn<?> column) {
        final int index = conditionIndexes.getOrDefault(column, -1);
        if (index == -1) {
            throw new IllegalArgumentException("no condition for column: name=" + column.getName());
        }
        return index;
    }

    protected final <T> void setCondition(final PreparedStatement stmt, final DbColumn<T> column, final int offset,
                                 final T value) throws SQLException {
        column.setParameter(stmt, offset + getConditionIndex(column) + 1, value);
    }

    protected final void appendWhereSQL(final StringBuilder sb) {
        if (!conditions.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(conditions.stream().map(DbCondition::getSQL).collect(Collectors.joining(" AND ")));
        }
    }

}
