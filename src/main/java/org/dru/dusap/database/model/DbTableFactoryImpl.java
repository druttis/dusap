package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;
import org.dru.dusap.database.type.DbTypes;

import java.util.Objects;

public final class DbTableFactoryImpl implements DbTableFactory, DbContext {
    private final DbTypes dbTypes;

    public DbTableFactoryImpl(final DbTypes dbTypes) {
        this.dbTypes = dbTypes;
    }

    @Override
    public <T> DbTable<T> newTable(final String name, final Class<T> type) {
        Objects.requireNonNull(type, "type");
        return new DbTable<>(this, name, type);
    }

    @Override
    public DbTable<?> newTable(final String name) {
        return new DbTable<>(this, name, null);
    }

    @Override
    public <T> DbType<T> getDbType(final Class<T> type) {
        return dbTypes.getDbType(type);
    }
}
