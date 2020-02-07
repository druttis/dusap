package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.Function;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
    R apply(T t) throws E;

    static <T, R> Function<T, R> wrap(final ThrowingFunction<T, R, ?> throwingFunction) {
        Objects.requireNonNull(throwingFunction, "function");
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }
}
