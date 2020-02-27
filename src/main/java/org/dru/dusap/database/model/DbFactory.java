package org.dru.dusap.database.model;

public interface DbFactory {
    DbTable<?> newTable(String name);

    <T> DbTable<T> newTable(String name, Class<T> type);
}
