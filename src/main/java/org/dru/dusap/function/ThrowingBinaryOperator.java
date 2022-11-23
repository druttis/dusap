package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingBinaryOperator<T, E extends Throwable> extends ThrowingBiFunction<T, T, T, E> {
}
