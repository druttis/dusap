package org.dru.dusap.database.model;

public interface DbFactory {
    <T> DbTable<T> newTable(String name, Class<T> type);

    DbTable<?> newTable(String name);
}
