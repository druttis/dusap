package org.dru.dusap.inject.provider;

import javax.inject.Provider;
import java.util.Objects;

public final class ValueProvider<T> implements Provider<T> {
    private final T value;

    public ValueProvider(final T value) {
        Objects.requireNonNull(value, "value");
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
