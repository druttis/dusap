package org.dru.dusap.data;

import java.util.Objects;

public final class DataUtils {
    public static int bitsInBytes(final int bits) {
        return (bits + 7) >> 3;
    }

    public static int bytesInBits(final int bytes) {
        return bytes << 3;
    }

    public static int varLength(final int bits) {
        return Math.max(1, (bits + 6) / 7);
    }

    public static int varLength(final long val, final int maxBits) {
        final long mask = (maxBits < 64 ? (1L << maxBits) -1L : -1L);
        return varLength(64 - Long.numberOfLeadingZeros(val & mask));
    }

    public static int utfLength(final String str) {
        Objects.requireNonNull(str, "str");
        final int strLength = str.length();
        int utfLength = strLength;
        for (int index = 0; index < strLength; index++) {
            final int ch = str.charAt(index);
            if (ch >= 128 || ch == 0) {
                utfLength += (ch >= 2048 ? 2 : 1);
            }
        }
        return utfLength;
    }

    private DataUtils() {
    }
}
