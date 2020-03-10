package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.time.TimeSupplier;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class DbShardStore<K, V> implements DbStore<K, V> {
    private final int shardNum;
    private final DbExecutor dbExecutor;
    private final DbStoreSupport<K, V> dbStoreSupport;
    private final TimeSupplier timeSupplier;

    DbShardStore(final int shardNum, final DbExecutor dbExecutor, final DbStoreSupport<K, V> dbStoreSupport,
                 final TimeSupplier timeSupplier) {
        this.shardNum = shardNum;
        this.dbExecutor = dbExecutor;
        this.dbStoreSupport = dbStoreSupport;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public Map<K, V> getAll(final Collection<K> keys, final boolean locked) throws SQLException {
        return dbExecutor.query(shardNum, conn -> dbStoreSupport.getAll(conn, keys, locked));
    }

    @Override
    public V get(final K key, final boolean locked) throws SQLException {
        return dbExecutor.query(shardNum, conn -> dbStoreSupport.get(conn, key, locked));
    }

    @Override
    public void putAll(final Map<K, V> entries) throws SQLException {
        dbExecutor.execute(shardNum, conn -> dbStoreSupport.putAll(conn, entries, nowMs()));
    }

    @Override
    public void put(final K key, final V value) throws SQLException {
        dbExecutor.execute(shardNum, conn -> dbStoreSupport.put(conn, key, value, nowMs()));
    }

    @Override
    public Map<K, V> computeAll(final Collection<K> keys, final UnaryOperator<V> operator) throws SQLException {
        return dbExecutor.update(shardNum, conn -> dbStoreSupport.computeAll(conn, keys, operator, nowMs()));
    }

    @Override
    public V compute(final K key, final UnaryOperator<V> operator) throws SQLException {
        return dbExecutor.update(shardNum, conn -> dbStoreSupport.compute(conn, key, operator, nowMs()));
    }

    @Override
    public Set<K> removeAll(final Collection<K> keys) throws SQLException {
        return dbExecutor.update(shardNum, conn -> dbStoreSupport.removeAll(conn, keys, nowMs()));
    }

    @Override
    public boolean remove(final K key) throws SQLException {
        return dbExecutor.update(shardNum, conn -> dbStoreSupport.remove(conn, key, nowMs()));
    }

    private long nowMs() {
        return timeSupplier.get().toEpochMilli();
    }
}
