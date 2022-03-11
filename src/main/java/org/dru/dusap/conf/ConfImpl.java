package org.dru.dusap.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfImpl implements Conf {
    private final Map<String, Properties> propertiesByPrefix;

    public ConfImpl() {
        propertiesByPrefix = new ConcurrentHashMap<>();
    }

    @Override
    public String getOrDefault(final String key, final String defaultValue) {
        Objects.requireNonNull(key, "key");
        final int index = key.indexOf(".");
        if (index == -1) {
            throw new IllegalArgumentException("Illegal key format: " + key);
        }
        final String prefix = key.substring(0, index);
        try {
            final Properties properties = propertiesByPrefix.computeIfAbsent(prefix, $ -> loadProperties(prefix));
            return properties.getProperty(key, defaultValue);
        } catch (final RuntimeException exc) {
            if (defaultValue != null) {
                return defaultValue;
            } else {
                throw exc;
            }
        }
    }

    @Override
    public String get(final String key) {
        return getOrDefault(key, null);
    }

    private Properties loadProperties(final String prefix) {
        final String filename = String.format("%s.properties", prefix);
        try (final InputStream in = Conf.class.getResourceAsStream("/" + filename)) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;

        } catch (final IOException exc) {
            throw new RuntimeException("Failed to load properties: " + filename, exc);
        }
    }
}
