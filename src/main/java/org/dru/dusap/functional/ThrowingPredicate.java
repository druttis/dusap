package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.Predicate;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Throwable> {
    boolean test(T t) throws E;

    static <T> Predicate<T> wrap(final ThrowingPredicate<T, ?> throwingPredicate) {
        Objects.requireNonNull(throwingPredicate, "throwingPredicate");
        return t -> {
            try {
                return throwingPredicate.test(t);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }
}
