package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
    R apply(T t, U u) throws E;
}
