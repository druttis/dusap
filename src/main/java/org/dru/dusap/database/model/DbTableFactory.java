package org.dru.dusap.database.model;

public interface DbTableFactory {
    <T> DbTable<T> newTable(String name, Class<T> type);

    DbTable<?> newTable(String name);
}
