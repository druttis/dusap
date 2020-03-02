package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;

public interface DbContext {
    <T> DbType<T> getType(Class<T> type);
}
