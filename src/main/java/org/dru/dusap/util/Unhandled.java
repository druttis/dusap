package org.dru.dusap.util;

public final class Unhandled {
    public static void unhandledRuntimeException(final Runnable action) {
        try {
            action.run();
        } catch (final RuntimeException exc) {
            System.err.println("Unhandled RuntimeException caught:");
            exc.printStackTrace();
        }
    }

    private Unhandled() {
    }
}
