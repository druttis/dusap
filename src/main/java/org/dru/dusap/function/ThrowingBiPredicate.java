package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, E extends Throwable> {
    boolean test(T t, U u) throws E;
}
