package org.dru.dusap.database.executor;

public interface DbExecutorManager {
    DbExecutor getExecutor(String name, int shardNum);

    DbExecutor getExecutor(String name);
}
