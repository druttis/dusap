package org.dru.dusap.database.model;

public interface DbEntityVisitor<R, D> {
    <C> R visitColumn(DbColumn<C> column, D data);

    <T> R visitTable(DbTable<T> table, D data);
}
