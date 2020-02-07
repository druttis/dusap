package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.ToIntFunction;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingToIntFunction<T, E extends Throwable> {
    int apply(T value) throws E;

    static <T> ToIntFunction<T> wrap(final ThrowingToIntFunction<T, ?> throwingToIntFunction) {
        Objects.requireNonNull(throwingToIntFunction, "throwingToIntFunction");
        return value -> {
            try {
                return throwingToIntFunction.apply(value);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }
}
