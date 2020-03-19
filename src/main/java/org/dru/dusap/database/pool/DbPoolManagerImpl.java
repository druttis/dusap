package org.dru.dusap.database.pool;

import org.dru.dusap.database.config.DbConfig;
import org.dru.dusap.time.TimeSupplier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DbPoolManagerImpl implements DbPoolManager {
    private final TimeSupplier timeSupplier;
    private final Map<String, List<DbPool>> poolsByClusterName;

    public DbPoolManagerImpl(final TimeSupplier timeSupplier) {
        this.timeSupplier = timeSupplier;
        poolsByClusterName = new ConcurrentHashMap<>();
    }

    @Override
    public List<DbPool> getPools(final String clusterName) {
        final List<DbPool> pools = poolsByClusterName.get(clusterName);
        if (pools == null) {
            throw new IllegalArgumentException("no such pool cluster: name=" + clusterName);
        }
        return pools;
    }

    @Override
    public DbPool getPool(final String clusterName, final int shard) {
        final List<DbPool> pools = getPools(clusterName);
        if (shard < 0) {
            throw new IllegalArgumentException("negative shard: " + shard);
        }
        if (shard >= pools.size()) {
            throw new ArrayIndexOutOfBoundsException("shard out of range: " + shard
                    + ", maximum cluster shard: " + (pools.size() - 1));
        }
        return pools.get(shard);
    }

    @Override
    public void addConfig(final DbConfig config) {
        final String clusterName = config.getName();
        poolsByClusterName.compute(clusterName, ($, pools) -> {
            if (pools != null) {
                throw new IllegalStateException("cluster already exist: " + clusterName);
            }
            return Collections.unmodifiableList(config.getShardConfigs().stream()
                    .map(shardConfig -> new DbPoolImpl(timeSupplier,
                            config.getMinimumConnectionsPerShard(),
                            config.getMaximumConnectionsPerShard(),
                            shardConfig))
                    .collect(Collectors.toList()));
        });
    }
}
