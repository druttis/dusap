package org.dru.dusap.database.config;

import java.util.Objects;
import java.util.Properties;

public final class DbShardConfig {
    private String driverClassName;
    private String url;
    private String user;
    private String password;
    private Properties properties;

    public DbShardConfig(final String driverClassName, final String url, final String user, final String password,
                         final Properties properties) {
        this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName");
        this.url = Objects.requireNonNull(url, "url");
        this.user = user;
        this.password = password;
        this.properties = properties;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DbShardConfig)) return false;
        final DbShardConfig that = (DbShardConfig) o;
        return getDriverClassName().equals(that.getDriverClassName()) &&
                getUrl().equals(that.getUrl()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getProperties(), that.getProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDriverClassName(), getUrl(), getUser(), getPassword(), getProperties());
    }

    @Override
    public String toString() {
        return "DbShardConfig{" +
                "driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", properties=" + properties +
                '}';
    }
}
