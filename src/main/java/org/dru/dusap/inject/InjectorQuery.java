package org.dru.dusap.inject;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static org.dru.dusap.inject.Keys.key;

public final class InjectorQuery<T> {
    public static <T> InjectorQuery<T> of(final Key<T> key) {
        return new InjectorQuery<>(key, null);
    }

    public static <T> InjectorQuery<T> of(final Key<T> key, final Class<? extends InjectorModule> target) {
        Objects.requireNonNull(target, "target");
        return new InjectorQuery<>(key, target);
    }

    public static <T> InjectorQuery<T> of(final Key<T> key, final AnnotatedElement element) {
        Objects.requireNonNull(element, "element");
        if (element.isAnnotationPresent(Source.class)) {
            return of(key, element.getAnnotation(Source.class).value());
        } else {
            return of(key);
        }
    }

    public static InjectorQuery<?> of(final Field field) {
        return of(key(field), field);
    }

    public static InjectorQuery<?> of(final Parameter parameter) {
        return of(key(parameter), parameter);
    }

    private final Key<T> key;
    private final Class<? extends InjectorModule> target;

    private InjectorQuery(final Key<T> key, final Class<? extends InjectorModule> target) {
        Objects.requireNonNull(key, "key");
        this.key = key;
        this.target = target;
    }

    public Key<T> getKey() {
        return key;
    }

    public Class<? extends InjectorModule> getTarget() {
        return target;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InjectorQuery<?> query = (InjectorQuery<?>) o;
        return getKey().equals(query.getKey()) && getTarget().equals(query.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getTarget());
    }

    @Override
    public String toString() {
        return "Query{" +
                "key=" + key +
                ", target=" + target +
                '}';
    }
}
