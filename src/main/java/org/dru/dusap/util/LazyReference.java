package org.dru.dusap.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class LazyReference<T> implements Reference<T>, Resettable {
    public static <T> LazyReference<T> by(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return new LazyReference<>(supplier);
    }

    private static final Object NULL = new Object();

    private final Supplier<? extends T> supplier;
    private final AtomicReference<Object> atomicReference;

    private LazyReference(final Supplier<? extends T> supplier) {
        this.supplier = supplier;
        atomicReference = new AtomicReference<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        Object instance = atomicReference.get();
        if (instance == null) {
            instance = supplier.get();
            if (instance == null) {
                instance = NULL;
            }
            if (!atomicReference.compareAndSet(null, instance)) {
                instance = atomicReference.get();
            }
        }
        return (instance == NULL ? null : (T) instance);
    }

    @Override
    public void reset() {
        atomicReference.set(null);
    }
}
