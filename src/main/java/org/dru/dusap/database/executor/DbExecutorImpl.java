package org.dru.dusap.database.executor;

import org.dru.dusap.database.pool.DbPoolManager;
import org.dru.dusap.functional.ThrowingConsumer;
import org.dru.dusap.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;

public final class DbExecutorImpl implements DbExecutor {
    private final DbPoolManager poolManager;
    private final String clusterName;

    public DbExecutorImpl(final DbPoolManager poolManager, final String clusterName) {
        this.poolManager = poolManager;
        this.clusterName = clusterName;
    }

    @Override
    public <T> T query(final int shard, final ThrowingFunction<Connection, T, SQLException> command)
            throws SQLException {
        final Connection conn = acquire(shard);
        try {
            return command.apply(conn);
        } finally {
            release(shard, conn);
        }
    }

    @Override
    public <T> T update(final int shard, final ThrowingFunction<Connection, T, SQLException> command)
            throws SQLException {
        final Connection conn = acquire(shard);
        try {
            final boolean autoCommit = conn.getAutoCommit();
            try {
                // ensure auto-commit is off.
                if (autoCommit) {
                    conn.setAutoCommit(false);
                }
                // execute the update, commit and return the result.
                final T result = command.apply(conn);
                conn.commit();
                return result;
            } finally {
                // restore auto-commit if needed.
                if (autoCommit) {
                    conn.setAutoCommit(true);
                }
            }
        } catch (final Throwable exc) {
            // rollback on any throwable
            rollback(conn);
            throw exc;
        } finally {
            release(shard, conn);
        }
    }

    @Override
    public void update(final int shard, final ThrowingConsumer<Connection, SQLException> command)
            throws SQLException {
        update(shard, (ThrowingFunction<Connection, Void, SQLException>) connection -> {
            command.accept(connection);
            return null;
        });
    }

    private Connection acquire(final int shard) throws SQLException {
        try {
            return poolManager.getPool(clusterName, shard).acquire();
        } catch (final InterruptedException exc) {
            throw new SQLException("interrupted", exc);
        }
    }

    private void release(final int shard, final Connection conn) {
        poolManager.getPool(clusterName, shard).release(conn);
    }

    private void rollback(final Connection connection) {
        try {
            connection.rollback();
        } catch (final SQLException exc) {
            // ignore
        }
    }
}
