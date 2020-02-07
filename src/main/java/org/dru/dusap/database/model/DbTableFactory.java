package org.dru.dusap.database.model;

public interface DbTableFactory {
    <T> DbTable<T> createTable(final String name, final Class<T> type);

    <T> DbTableBuilder<T> createBuilder(final String name, final Class<T> type);
}
