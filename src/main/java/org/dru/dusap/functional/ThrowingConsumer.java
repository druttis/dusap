package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.Consumer;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws E;

    static <T> Consumer<T> wrap(final ThrowingConsumer<T, ?> throwingConsumer) {
        Objects.requireNonNull(throwingConsumer, "throwingConsumer");
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }
}
