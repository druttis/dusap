package org.dru.dusap.database.config;

public interface DbConfigManager {
    DbConfig getConfig(String name);

    void addConfig(final DbConfig config);
}
