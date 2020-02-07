package org.dru.dusap.database.executor;

import org.dru.dusap.database.pool.DbPoolManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DbExecutorProviderImpl implements DbExecutorProvider {
    private final DbPoolManager poolManager;
    private final Map<String, DbExecutor> executorByClusterName;

    public DbExecutorProviderImpl(final DbPoolManager poolManager) {
        this.poolManager = poolManager;
        executorByClusterName = new ConcurrentHashMap<>();
    }

    @Override
    public DbExecutor getExecutor(final String clusterName) {
        return executorByClusterName.computeIfAbsent(clusterName, $ ->
                new DbExecutorImpl(poolManager, clusterName));
    }
}
