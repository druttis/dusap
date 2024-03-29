package org.dru.dusap.function;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
