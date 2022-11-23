package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Throwable> {
    boolean test(T t) throws E;
}
