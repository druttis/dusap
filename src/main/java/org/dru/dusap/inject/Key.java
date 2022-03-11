package org.dru.dusap.inject;

import org.dru.dusap.util.TypeLiteral;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.dru.dusap.util.ReflectionUtils.getAnnotatedAnnotations;

public final class Key<T> {
    public static Key<?> of(final Field field) {
        Objects.requireNonNull(field, "field");
        final KeyBuilder<?> keyBuilder = KeyBuilder.of(field.getGenericType());
        getAnnotatedAnnotations(field, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    public static Key<?> of(final Method method) {
        Objects.requireNonNull(method, "method");
        final KeyBuilder<?> keyBuilder = KeyBuilder.of(method.getGenericReturnType());
        getAnnotatedAnnotations(method, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    public static Key<?> of(final Parameter parameter) {
        Objects.requireNonNull(parameter, "parameter");
        final KeyBuilder<?> keyBuilder = KeyBuilder.of(parameter.getParameterizedType());
        getAnnotatedAnnotations(parameter, Qualifier.class).forEach(keyBuilder::with);
        return keyBuilder.build();
    }

    private final TypeLiteral<T> type;
    private final Set<? extends Annotation> qualifiers;

    Key(final TypeLiteral<T> type, final Collection<? extends Annotation> qualifiers) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(qualifiers, "qualifiers");
        this.type = type;
        this.qualifiers = Collections.unmodifiableSet(new HashSet<>(qualifiers));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Key<?>)) return false;
        final Key<?> key = (Key<?>) o;
        return type.equals(key.type) && qualifiers.equals(key.qualifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, qualifiers);
    }

    @Override
    public String toString() {
        return "Key{" +
                "type=" + type +
                ", qualifiers=" + qualifiers +
                '}';
    }

    public TypeLiteral<T> getType() {
        return type;
    }

    public Set<? extends Annotation> getQualifiers() {
        return qualifiers;
    }
}
