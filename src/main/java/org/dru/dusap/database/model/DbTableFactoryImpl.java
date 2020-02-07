package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbTypes;

public final class DbTableFactoryImpl implements DbTableFactory {
    private final DbTypes dbTypes;

    public DbTableFactoryImpl(final DbTypes dbTypes) {
        this.dbTypes = dbTypes;
    }

    @Override
    public <T> DbTable<T> createTable(final String name, final Class<T> type) {
        return createBuilder(name, type).build();
    }

    @Override
    public <T> DbTableBuilder<T> createBuilder(final String name, final Class<T> type) {
        return new DbTableBuilder<>(dbTypes, name, type);
    }
}
