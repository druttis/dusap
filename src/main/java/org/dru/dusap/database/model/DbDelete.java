package org.dru.dusap.database.model;

public final class DbDelete extends DbStatement {
    public static DbDelete all() {
        return new DbDelete();
    }

    public static DbDelete where(final DbColumn<?> column, final String op) {
        return new DbDelete();
    }

    private DbDelete() {
    }

    public DbDelete and(final DbColumn<?> column, final String op) {
        addCondition(column, op);
        return this;
    }

    public <C> DbDelete and(final DbColumn<C> column, final String op, final C value) {
        addCondition(column, op, value);
        return this;
    }

    public <C> DbDelete and(final DbColumn<C> column, final String op, final DbColumn<C> other) {
        addCondition(column, op, other);
        return this;
    }
}
