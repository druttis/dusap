package org.dru.dusap.database.pool;

import org.dru.dusap.database.config.DbClusterConfig;

import java.util.List;

public interface DbPoolManager {
    List<DbPool> getPools(String clusterName);

    DbPool getPool(String clusterName, final int shard);

    void addConfig(DbClusterConfig config);
}
