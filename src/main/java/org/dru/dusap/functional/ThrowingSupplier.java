package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.Supplier;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;

    static <T> Supplier<T> wrap(final ThrowingSupplier<T, ?> throwingSupplier) {
        Objects.requireNonNull(throwingSupplier, "throwingSupplier");
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }
}
