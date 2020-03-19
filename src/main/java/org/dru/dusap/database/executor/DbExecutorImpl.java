package org.dru.dusap.database.executor;

import org.dru.dusap.database.pool.DbPool;
import org.dru.dusap.functional.ThrowingConsumer;
import org.dru.dusap.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class DbExecutorImpl implements DbExecutor {
    private static final ThreadLocal<Connection> threadConn = new ThreadLocal<>();

    private final DbPool dbPool;

    public DbExecutorImpl(final DbPool dbPool) {
        Objects.requireNonNull(dbPool, "dbPool");
        this.dbPool = dbPool;
    }

    @Override
    public <T> T invoke(final ThrowingFunction<Connection, T, SQLException> command) {
        try {
            try {
                return command.apply(acquire());
            } catch (final SQLException exc) {
                throw new RuntimeException(exc);
            }
        } catch (final RuntimeException exc) {
            rollback();
            throw exc;
        }
    }

    @Override
    public final void execute(final ThrowingConsumer<Connection, SQLException> command) {
        invoke((ThrowingFunction<Connection, Void, SQLException>) connection -> {
            command.accept(connection);
            return null;
        });
    }

    @Override
    public void beginTransaction() {
        if (threadConn.get() == null) {
            try {
                final Connection conn = dbPool.acquire();
                try {
                    conn.setAutoCommit(false);
                } catch (final SQLException exc) {
                    throw new RuntimeException(exc);
                }
                threadConn.set(conn);
            } catch (final InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        } else {
            throw new IllegalStateException("already in transaction");
        }
    }

    @Override
    public void commit() {
        final Connection conn = threadConn.get();
        if (conn != null) {
            threadConn.remove();
            try {
                conn.commit();
                conn.setAutoCommit(true);
                dbPool.release(conn);
            } catch (final SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    public void rollback() {
        final Connection conn = threadConn.get();
        if (conn != null) {
            threadConn.remove();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                dbPool.release(conn);
            } catch (final SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    protected final DbPool getDbPool() {
        return dbPool;
    }

    protected final Connection acquire() {
        Connection conn = threadConn.get();
        if (conn == null) {
            try {
                conn = dbPool.acquire();
            } catch (final InterruptedException exc) {
                throw new RuntimeException("interrupted", exc);
            }
        }
        return conn;
    }
}
