package org.dru.dusap.cache;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public final class CacheEntry<V> implements Serializable {
    private final V value;
    private final Instant expires;

    public CacheEntry(final V value, final Instant expires) {
        Objects.requireNonNull(expires, "expires");
        this.value = value;
        this.expires = expires;
    }

    public V getValue() {
        return value;
    }

    public Instant getExpires() {
        return expires;
    }

    public boolean hasExpired(final Instant now) {
        Objects.requireNonNull(now, "now");
        return expires.isBefore(now);
    }
}
