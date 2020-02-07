package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
    R apply(T t, U u) throws E;

    static <T, U, R> BiFunction<T, U, R> wrap(final ThrowingBiFunction<T, U, R, ?> throwingBiFunction) {
        Objects.requireNonNull(throwingBiFunction, "throwingBiFunction");
        return (t, u) -> {
            try {
                return throwingBiFunction.apply(t, u);
            } catch (final Throwable throwable) {
                return Throwing.raise(throwable);
            }
        };
    }
}
