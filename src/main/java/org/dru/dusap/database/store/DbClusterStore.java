package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.reflection.ReflectionUtils;
import org.dru.dusap.util.JumpConsistentHash;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class DbClusterStore<K, V> implements DbStore<K, V> {
    private final List<DbStore<K, V>> shards;

    public DbClusterStore(final List<DbStore<K, V>> shards) {
        this.shards = shards;
    }

    @Override
    public V get(final K key) throws SQLException {
        final Item<K, V> item = lookup(key);
        final V value = item.value;
        if (value != null) {
            final DbStore<K, V> shard = getShard(key);
            if (item.shard != shard) {
                item.shard.delete(key);
            }
        }
        return value;
    }

    @Override
    public void set(final K key, final V value) throws SQLException {
        if (value == null) {
            delete(key);
        } else {
            final Item<K, V> oldItem = lookup(key);
            final V oldValue = oldItem.value;
            final DbStore<K, V> shard = getShard(key);
            if (!value.equals(oldValue)) {
                shard.set(key, value);
            }
            if (oldValue != null && oldItem.shard != shard) {
                oldItem.shard.delete(key);
            }
        }
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) throws SQLException {
        final Item<K, V> oldItem = lookup(key);
        final V oldValue = oldItem.value;
        final V value = operator.apply(ReflectionUtils.copyInstance(oldValue));
        if (value == null) {
            if (oldValue != null) {
                oldItem.shard.delete(key);
            }
        } else {
            final DbStore<K, V> shard = getShard(key);
            if (!value.equals(oldValue)) {
                shard.set(key, value);
            }
            if (oldValue != null && oldItem.shard != shard) {
                oldItem.shard.delete(key);
            }
        }
        return value;
    }

    @Override
    public boolean delete(final K key) throws SQLException {
        final List<DbStore<K, V>> shardList = getShardList(key);
        for (final DbStore<K, V> shard : shardList) {
            if (shard.delete(key)) {
                return true;
            }
        }
        return false;
    }

    public int getNumShards() {
        return shards.size();
    }

    public DbStore<K, V> getShard(final int shardNum) {
        return shards.get(shardNum);
    }

    private DbStore<K, V> getShard(final K key, final int numShards) {
        return getShard(JumpConsistentHash.hash(key, numShards));
    }

    private DbStore<K, V> getShard(final K key) {
        return getShard(key, getNumShards());
    }

    private List<DbStore<K, V>> getShardList(final K key) {
        final List<DbStore<K, V>> shardList = new ArrayList<>();
        final Set<DbStore<K, V>> visitedShards = new HashSet<>();
        for (int numShards = getNumShards(); --numShards >= 1; ) {
            final DbStore<K, V> shard = getShard(key, numShards);
            if (visitedShards.add(shard)) {
                shardList.add(shard);
            }
        }
        return shardList;
    }

    private Item<K, V> lookup(final K key) throws SQLException {
        final List<DbStore<K, V>> shardList = getShardList(key);
        for (final DbStore<K, V> shard : shardList) {
            final V value = shard.get(key);
            if (value != null) {
                return new Item<>(value, shard);
            }
        }
        return new Item<>(null, null);
    }

    private static final class Item<K, V> {
        private final V value;
        private final DbStore<K, V> shard;

        private Item(final V value, final DbStore<K, V> shard) {
            this.value = value;
            this.shard = shard;
        }
    }
}
