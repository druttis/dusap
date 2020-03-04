package org.dru.dusap.database.model;

import org.dru.dusap.database.executor.DbExecutor;

public interface DbTableManager {
    void createTableIfNotExist(DbExecutor executor, int shard, DbTable<?> table);
}
