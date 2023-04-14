package org.dru.dusap.serialization;

import java.util.Arrays;

public abstract class AbstractTypeSerializer<T> implements TypeSerializer<T> {
    private final Class<?>[] types;

    public AbstractTypeSerializer(final Class<?>... types) {
        this.types = types;
    }

    @Override
    public final Class<?>[] getTypes() {
        return Arrays.copyOf(types, types.length);
    }
}
