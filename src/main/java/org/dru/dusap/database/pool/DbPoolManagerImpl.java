package org.dru.dusap.database.pool;

import org.dru.dusap.database.config.DbConfig;
import org.dru.dusap.database.config.DbConfigManager;
import org.dru.dusap.time.TimeSupplier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DbPoolManagerImpl implements DbPoolManager {
    private final DbConfigManager dbConfigManager;
    private final TimeSupplier timeSupplier;
    private final Map<String, List<DbPool>> poolsByName;

    public DbPoolManagerImpl(final DbConfigManager dbConfigManager, final TimeSupplier timeSupplier) {
        this.dbConfigManager = dbConfigManager;
        this.timeSupplier = timeSupplier;
        poolsByName = new ConcurrentHashMap<>();
    }

    @Override
    public List<DbPool> getPools(final String name) {
        final List<DbPool> pools = poolsByName.computeIfAbsent(name, ($) -> {
            final DbConfig config = dbConfigManager.getConfig(name);
            return Collections.unmodifiableList(config.getBucketConfigs().stream()
                    .map(shardConfig -> new DbPoolImpl(timeSupplier,
                            config.getMinConnectionsPerBucket(),
                            config.getMaxConnectionsPerBucket(),
                            shardConfig))
                    .collect(Collectors.toList()));
        });
        return pools;
    }

    @Override
    public DbPool getPool(final String name, final int bucketNum) {
        final List<DbPool> pools = getPools(name);
        if (bucketNum < 0) {
            throw new IllegalArgumentException("negative shard: " + bucketNum);
        }
        if (bucketNum >= pools.size()) {
            throw new ArrayIndexOutOfBoundsException("shard out of range: " + bucketNum
                    + ", maximum cluster shard: " + (pools.size() - 1));
        }
        return pools.get(bucketNum);
    }
}
