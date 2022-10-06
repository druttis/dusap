package org.dru.dusap.cache.lru;

import java.time.Instant;

final class LruEntry<V> {
    private final V value;
    private final Instant expires;

    LruEntry(final V value, final Instant expires) {
        this.value = value;
        this.expires = expires;
    }

    V getValue() {
        return value;
    }

    boolean hasExpired(final Instant when) {
        return expires.isBefore(when);
    }
}
