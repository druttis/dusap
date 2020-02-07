package org.dru.dusap.functional;

import java.util.Objects;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;

    static Runnable wrap(final ThrowingRunnable<?> throwingRunnable) {
        Objects.requireNonNull(throwingRunnable, "throwingRunnable");
        return () -> {
            try {
                throwingRunnable.run();
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }
}
