package org.dru.dusap.database.executor;

public interface DbExecutorManager {
    DbExecutor getExecutor(String name, int bucketNum);

    DbExecutor getExecutor(String name);
}
