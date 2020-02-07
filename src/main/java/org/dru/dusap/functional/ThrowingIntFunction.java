package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.IntFunction;

public interface ThrowingIntFunction<R, E extends Throwable> {
    R apply(int value) throws E;

    static <R> IntFunction<R> wrap(final ThrowingIntFunction<R, ?> throwingIntFunction) {
        Objects.requireNonNull(throwingIntFunction, "throwingIntFunction");
        return value -> {
            try {
                return throwingIntFunction.apply(value);
            } catch (final Throwable throwable) {
                return Throwing.raise(throwable);
            }
        };
    }
}
