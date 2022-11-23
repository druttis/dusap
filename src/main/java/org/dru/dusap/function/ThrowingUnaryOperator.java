package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingUnaryOperator<T, E extends Throwable> extends ThrowingFunction<T, T, E> {
}
