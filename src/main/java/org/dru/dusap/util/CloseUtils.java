package org.dru.dusap.util;

public final class CloseUtils {
    public static void close(final AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (final Exception exc) {
                // ignore.
            }
        }
    }

    private CloseUtils() {
    }
}
