package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, E extends Throwable> {
    boolean test(T t, U u) throws E;

    static <T, U> BiPredicate<T, U> wrap(final ThrowingBiPredicate<T, U, ?> throwingBiPredicate) {
        Objects.requireNonNull(throwingBiPredicate, "throwingBiPredicate");
        return (t, u) -> {
            try {
                return throwingBiPredicate.test(t, u);
            } catch (final Throwable throwable) {
                return Throwing.raise(throwable);
            }
        };
    }
}
