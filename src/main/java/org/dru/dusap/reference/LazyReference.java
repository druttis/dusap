package org.dru.dusap.reference;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class LazyReference<T> implements Reference<T> {
    private static final Object NULL = new Object();

    public static <T> LazyReference<T> by(final Supplier<? extends T> supplier) {
        return new LazyReference<>(supplier);
    }

    private final Supplier<? extends T> supplier;
    private final AtomicReference<Object> instanceRef;

    private LazyReference(final Supplier<? extends T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier");
        instanceRef = new AtomicReference<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        Object instance = instanceRef.get();
        if (instance == null) {
            instance = supplier.get();
            if (instance == null) {
                instance = NULL;
            }
            if (!instanceRef.compareAndSet(null, instance)) {
                instance = instanceRef.get();
            }
        }
        return (T) (instance == NULL ? null : instance);
    }
}
