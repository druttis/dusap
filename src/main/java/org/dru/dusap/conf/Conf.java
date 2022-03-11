package org.dru.dusap.conf;

public interface Conf {
    String getOrDefault(String key, final String defaultValue);

    String get(String key);
}
