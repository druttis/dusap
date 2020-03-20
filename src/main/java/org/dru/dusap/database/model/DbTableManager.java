package org.dru.dusap.database.model;

import org.dru.dusap.database.executor.DbExecutor;

public interface DbTableManager {
    void createTableIfNotExist(DbExecutor executor, int bucketNum, DbTable<?> table);
}
