package org.dru.dusap.inject;

import org.dru.dusap.util.TypeLiteral;

import javax.inject.Qualifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Objects;

import static org.dru.dusap.reflection.Reflections.getAnnotationsAnnotatedWith;

public final class Keys {
    public static <T> KeyBuilder<T> key(final TypeLiteral<T> typeLiteral) {
        return new KeyBuilder<>(typeLiteral);
    }

    public static <T> KeyBuilder<T> key(final Class<T> type) {
        return key(TypeLiteral.of(type));
    }

    public static KeyBuilder<?> key(final Type type) {
        return key(TypeLiteral.of(type));
    }

    public static Key<?> key(final Field field) {
        Objects.requireNonNull(field, "field");
        final KeyBuilder<?> keyBuilder = key(field.getGenericType());
        getAnnotationsAnnotatedWith(field, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    public static Key<?> key(final Method method) {
        Objects.requireNonNull(method, "method");
        final KeyBuilder<?> keyBuilder = key(method.getGenericReturnType());
        getAnnotationsAnnotatedWith(method, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    public static Key<?> key(final Parameter parameter) {
        Objects.requireNonNull(parameter, "parameter");
        final KeyBuilder<?> keyBuilder = key(parameter.getParameterizedType());
        getAnnotationsAnnotatedWith(parameter, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    private Keys() {
    }
}
