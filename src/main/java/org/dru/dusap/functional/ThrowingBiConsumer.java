package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> {
    void accept(T t, U u) throws E;

    static <T, U> BiConsumer<T, U> wrap(final ThrowingBiConsumer<T, U, ?> throwingBiConsumer) {
        Objects.requireNonNull(throwingBiConsumer, "throwingBiConsumer");
        return (t, u) -> {
            try {
                throwingBiConsumer.accept(t, u);
            } catch (final Throwable throwable) {
                Throwing.raise(throwable);
            }
        };
    }
}
