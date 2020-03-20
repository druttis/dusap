package org.dru.dusap.database.config;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class DbConfigManagerImpl implements DbConfigManager {
    private final Map<String, DbConfig> configByName;

    public DbConfigManagerImpl() {
        configByName = new ConcurrentHashMap<>();
    }

    @Override
    public DbConfig getConfig(final String name) {
        Objects.requireNonNull(name, "name");
        final DbConfig config = configByName.get(name);
        if (config == null) {
            throw new IllegalArgumentException("no such config: name=" + name);
        }
        return config;
    }

    @Override
    public void addConfig(final DbConfig config) {
        Objects.requireNonNull(config, "config");
        final String name = config.getName();
        if (configByName.computeIfAbsent(name, ($) -> config) != config) {
            throw new IllegalArgumentException("config already exist: name=" + name);
        }
    }
}
