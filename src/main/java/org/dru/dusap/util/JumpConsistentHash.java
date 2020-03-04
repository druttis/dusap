package org.dru.dusap.util;

import java.util.Objects;

public final class JumpConsistentHash {
    private static final long CONSTANT = 2862933555777941757L;
    private static final long JUMP = 1L << 31;
    private static final long UNSIGNED_MASK = 0x7fffffffffffffffL;

    private JumpConsistentHash() {
        throw new AssertionError("static");
    }

    public static int hash(final Object key, final int numBuckets) {
        Objects.requireNonNull(key, "key");
        return hash(key.hashCode(), numBuckets);
    }

    public static int hash(final long hash, final int numBuckets) {
        if (numBuckets < 0) {
            throw new IllegalArgumentException("size has to be 0 or greater: " + numBuckets);
        }
        long k = hash;
        long b = -1;
        long j = 0;
        while (j < numBuckets) {
            b = j;
            k = k * CONSTANT + 1L;
            j = Math.round((b + 1L) * (JUMP / toDouble((k >>> 33) + 1L)));
        }
        return (int) b;
    }

    public static double toDouble(final long n) {
        double d = n & UNSIGNED_MASK;
        if (n < 0L) {
            d += 0x1.0p63;
        }
        return d;
    }
}
