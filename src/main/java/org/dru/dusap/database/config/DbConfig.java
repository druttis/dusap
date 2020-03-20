package org.dru.dusap.database.config;

import org.dru.dusap.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class DbConfig {
    private String name;
    private int minConnectionsPerBucket;
    private int maxConnectionsPerBucket;
    private DbBucketConfig[] bucketConfigs;

    public DbConfig(final String name,
                    final int minConnectionsPerBucket, final int maxConnectionPerShard,
                    final DbBucketConfig[] bucketConfigs) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(bucketConfigs, "bucketConfigs");
        if (bucketConfigs.length == 0) {
            throw new IllegalArgumentException("at least one bucketConfig is required");
        }
        this.name = name;
        this.minConnectionsPerBucket = minConnectionsPerBucket;
        this.maxConnectionsPerBucket = maxConnectionPerShard;
        this.bucketConfigs = bucketConfigs;
    }

    public DbConfig(final String name,
                    final int minConnectionsPerBucket, final int maximumConnectionPerShard,
                    final Collection<DbBucketConfig> bucketConfigs) {
        this(name, minConnectionsPerBucket, maximumConnectionPerShard, bucketConfigs.toArray(new DbBucketConfig[0]));
    }

    public DbConfig(final String name,
                    final int minConnectionsPerBucket, final int maximumConnectionPerShard,
                    final DbBucketConfig first, final DbBucketConfig... rest) {
        this(name, minConnectionsPerBucket, maximumConnectionPerShard, CollectionUtils.asList(first, rest));
    }

    public String getName() {
        return name;
    }

    public int getMinConnectionsPerBucket() {
        return minConnectionsPerBucket;
    }

    public int getMaxConnectionsPerBucket() {
        return maxConnectionsPerBucket;
    }

    public int getNumBuckets() {
        return bucketConfigs.length;
    }

    public List<DbBucketConfig> getBucketConfigs() {
        return Arrays.asList(bucketConfigs);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DbConfig)) return false;
        final DbConfig that = (DbConfig) o;
        return name.equals(that.name) &&
                Arrays.equals(bucketConfigs, bucketConfigs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(bucketConfigs);
        return result;
    }

    @Override
    public String toString() {
        return "DbConfig{" +
                "name='" + name + '\'' +
                ", shardConfigs=" + Arrays.toString(bucketConfigs) +
                '}';
    }
}
