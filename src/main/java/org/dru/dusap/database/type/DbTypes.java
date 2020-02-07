package org.dru.dusap.database.type;

public interface DbTypes {
    <T> void registerDbType(final Class<T> type, final DbType<T> dbType);

    <T> DbType<T> getDefaultDbType(Class<T> type);
}
