package org.dru.dusap.functional;

import java.util.Objects;
import java.util.function.IntConsumer;

import static org.dru.dusap.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingIntConsumer<E extends Throwable> {
    void accept(int value) throws E;

    static IntConsumer wrap(final ThrowingIntConsumer<?> throwingIntConsumer) {
        Objects.requireNonNull(throwingIntConsumer, "throwingIntConsumer");
        return value -> {
            try {
                throwingIntConsumer.accept(value);
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }
}
