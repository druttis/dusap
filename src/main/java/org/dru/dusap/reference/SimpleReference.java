package org.dru.dusap.reference;

import java.util.Objects;

public final class SimpleReference<T> implements Reference<T> {
    public static <T> SimpleReference<T> of(final T instance) {
        return new SimpleReference<>(instance);
    }

    private final T instance;

    private SimpleReference(final T instance) {
        this.instance = Objects.requireNonNull(instance, "instance");
    }

    @Override
    public T get() {
        return instance;
    }
}
