package org.dru.dusap.functional;

import java.util.function.BinaryOperator;

public interface ThrowingBinaryOperator<T, E extends Throwable> extends ThrowingBiFunction<T, T, T, E> {
    static <T> BinaryOperator<T> wrap(final ThrowingBinaryOperator<T, ?> throwingBinaryOperator) {
        return (t, u) -> {
            try {
                return throwingBinaryOperator.apply(t, u);
            } catch (final Throwable throwable) {
                return Throwing.raise(throwable);
            }
        };
    }
}
