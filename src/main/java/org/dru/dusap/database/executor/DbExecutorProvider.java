package org.dru.dusap.database.executor;

public interface DbExecutorProvider {
    DbExecutor getExecutor(String clusterName);
}
