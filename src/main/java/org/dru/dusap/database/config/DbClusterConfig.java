package org.dru.dusap.database.config;

import org.dru.dusap.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Configuration of one or more shards-
 * A ClusterConfiguration containing only one shard could be thought of as a non-cluster configuration.
 */
public final class DbClusterConfig {
    private String name;
    private int minimumConnectionsPerShard;
    private int maximumConnectionsPerShard;
    private DbShardConfig[] shardConfigs;

    public DbClusterConfig(final String name,
                           final int minimumConnectionsPerShard, final int maximumConnectionPerShard,
                           final DbShardConfig[] shardConfigs) {
        this.name = Objects.requireNonNull(name, "name");
        this.minimumConnectionsPerShard = minimumConnectionsPerShard;
        this.maximumConnectionsPerShard = maximumConnectionPerShard;
        this.shardConfigs = Objects.requireNonNull(shardConfigs, "shardConfigs");
        if (shardConfigs.length == 0) {
            throw new IllegalArgumentException("expected at least one shard");
        }
    }

    public DbClusterConfig(final String name,
                           final int minimumConnectionsPerShard, final int maximumConnectionPerShard,
                           final Collection<DbShardConfig> shardConfigs) {
        this(name, minimumConnectionsPerShard, maximumConnectionPerShard, shardConfigs.toArray(new DbShardConfig[0]));
    }

    public DbClusterConfig(final String name,
                           final int minimumConnectionsPerShard, final int maximumConnectionPerShard,
                           final DbShardConfig first, final DbShardConfig... rest) {
        this(name, minimumConnectionsPerShard, maximumConnectionPerShard, CollectionUtils.asList(first, rest));
    }

    public String getName() {
        return name;
    }

    public int getMinimumConnectionsPerShard() {
        return minimumConnectionsPerShard;
    }

    public int getMaximumConnectionsPerShard() {
        return maximumConnectionsPerShard;
    }

    public List<DbShardConfig> getShardConfigs() {
        return Arrays.asList(shardConfigs);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DbClusterConfig)) return false;
        final DbClusterConfig that = (DbClusterConfig) o;
        return name.equals(that.name) &&
                Arrays.equals(shardConfigs, shardConfigs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(shardConfigs);
        return result;
    }

    @Override
    public String toString() {
        return "ClusterConfig{" +
                "name='" + name + '\'' +
                ", shards=" + Arrays.toString(shardConfigs) +
                '}';
    }
}
