package org.dru.dusap.database.pool;

import org.dru.dusap.database.config.DbShardConfig;
import org.dru.dusap.time.TimeSupplier;
import org.dru.dusap.util.AbstractPool;

import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public final class DbPoolImpl extends AbstractPool<Connection> implements DbPool {
    private final DbShardConfig config;

    public DbPoolImpl(final TimeSupplier timeSupplier, final int minimumSize, final int maximumSize,
                      final DbShardConfig config) {
        super(timeSupplier, minimumSize, maximumSize);
        this.config = Objects.requireNonNull(config, "config");
        finish();
    }

    @Override
    protected Connection create() {
        final Properties properties = new Properties();
        if (config.getProperties() != null) {
            properties.putAll(config.getProperties());
        }
        if (config.getUser() != null) {
            properties.setProperty("user", config.getUser());
        }
        if (config.getPassword() != null) {
            properties.setProperty("password", config.getPassword());
        }
        try {
            return DriverManager.getConnection(config.getUrl(), properties);
        } catch (final SQLException exc) {
            throw new RuntimeException("failed to get connection", exc);
        }
    }

    @Override
    protected boolean isValid(final Connection item) {
        try {
            try (final PreparedStatement stmt = item.prepareStatement("SELECT 1")) {
                try (final ResultSet rset = stmt.executeQuery()) {
                    rset.next();
                }
            }
        } catch (final SQLException exc) {
            return false;
        }
        return true;
    }

    @Override
    protected void destroy(final Connection item) {
        try {
            item.close();
        } catch (final SQLException exc) {
            // ignore
        }
    }
}
