package org.dru.dusap.database.model;

public interface DbVisitor<R, D> {
    <T> R visitColumn(DbColumn<T> column, D data);

    <T> R visitTable(DbTable<T> table, D data);
}
