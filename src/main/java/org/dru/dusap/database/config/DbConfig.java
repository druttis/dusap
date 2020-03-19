package org.dru.dusap.database.config;

import org.dru.dusap.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class DbConfig {
    private String name;
    private int minimumConnectionsPerShard;
    private int maximumConnectionsPerShard;
    private DbShardConfig[] shardConfigs;

    public DbConfig(final String name,
                    final int minimumConnectionsPerShard, final int maximumConnectionPerShard,
                    final DbShardConfig[] shardConfigs) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(shardConfigs, "shardConfigs");
        if (shardConfigs.length == 0) {
            throw new IllegalArgumentException("at least one shardConfig is required");
        }
        this.name = name;
        this.minimumConnectionsPerShard = minimumConnectionsPerShard;
        this.maximumConnectionsPerShard = maximumConnectionPerShard;
        this.shardConfigs = shardConfigs;
    }

    public DbConfig(final String name,
                    final int minimumConnectionsPerShard, final int maximumConnectionPerShard,
                    final Collection<DbShardConfig> shardConfigs) {
        this(name, minimumConnectionsPerShard, maximumConnectionPerShard, shardConfigs.toArray(new DbShardConfig[0]));
    }

    public DbConfig(final String name,
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
        if (!(o instanceof DbConfig)) return false;
        final DbConfig that = (DbConfig) o;
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
        return "DbConfig{" +
                "name='" + name + '\'' +
                ", shardConfigs=" + Arrays.toString(shardConfigs) +
                '}';
    }
}
