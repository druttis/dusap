package org.dru.dusap.database.pool;

import org.dru.dusap.database.config.DbConfig;

import java.util.List;

public interface DbPoolManager {
    List<DbPool> getPools(String clusterName);

    DbPool getPool(String clusterName, final int shard);

    void addConfig(DbConfig config);
}
