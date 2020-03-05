package org.dru.dusap.util;

import java.util.Objects;

public final class JumpConsistentHash {
    private JumpConsistentHash() {
        throw new AssertionError("static");
    }

    public static int hash(final Object key, final int numBuckets) {
        Objects.requireNonNull(key, "key");
        return hash(key.hashCode(), numBuckets);
    }

    public static int hash(final long key, final int numBuckets) {
        if (numBuckets < 1) {
            throw new IllegalArgumentException("numBuckets has to be 1 or greater: " + numBuckets);
        }
        long k = key;
        long b = -1;
        long j = 0;
        while (j < numBuckets) {
            b = j;
            k = k * 2862933555777941757L + 1L;
            j = Math.round((b + 1L) * (2147483648L / dbl((k >>> 33) + 1L)));
        }
        return (int) b;
    }

    private static double dbl(final long sl) {
        double ud = sl & 0x7fffffffffffffffL;
        if (sl < 0) {
            ud += 0x1.0p63;
        }
        return ud;
    }
}
