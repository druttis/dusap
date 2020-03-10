package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.time.TimeSupplier;

import java.sql.SQLException;
import java.util.function.UnaryOperator;

public final class DbShardStore<K, V> implements DbStore<K, V> {
    private final DbExecutor dbExecutor;
    private final int shardNum;
    private final DbStoreSupport<K, V> dbStoreSupport;
    private final TimeSupplier timeSupplier;

    public DbShardStore(final DbExecutor dbExecutor, final int shardNum, final DbStoreSupport<K, V> dbStoreSupport,
                        final TimeSupplier timeSupplier) {
        this.dbExecutor = dbExecutor;
        this.shardNum = shardNum;
        this.dbStoreSupport = dbStoreSupport;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public V get(final K key) throws SQLException {
        return dbExecutor.query(shardNum, conn -> dbStoreSupport.get(conn, key));
    }

    @Override
    public void set(final K key, final V value) throws SQLException {
        dbExecutor.updateNoResult(shardNum, conn -> dbStoreSupport.set(conn, key, value, getNowMs()));
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) throws SQLException {
        return dbExecutor.updateWithResult(shardNum, conn -> dbStoreSupport.update(conn, key, operator, getNowMs()));
    }

    @Override
    public boolean delete(final K key) throws SQLException {
        return dbExecutor.updateWithResult(0, conn -> dbStoreSupport.delete(conn, key));
    }

    private long getNowMs() {
        return timeSupplier.get().toEpochMilli();
    }
}
