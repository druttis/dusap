package org.dru.dusap.inject;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Objects;

public final class Query<T> {
    public static <T> Query<T> of(final Key<T> key) {
        return new Query<>(key, null);
    }

    public static <T> Query<T> of(final Key<T> key, final Class<? extends Module> target) {
        Objects.requireNonNull(target, "target");
        return new Query<>(key, target);
    }

    public static <T> Query<T> of(final Key<T> key, final AnnotatedElement element) {
        Objects.requireNonNull(element, "element");
        if (element.isAnnotationPresent(Source.class)) {
            return of(key, element.getAnnotation(Source.class).value());
        } else {
            return of(key);
        }
    }

    public static Query<?> of(final Field field) {
        return of(Key.of(field), field);
    }

    public static Query<?> of(final Parameter parameter) {
        return of(Key.of(parameter), parameter);
    }

    private final Key<T> key;
    private final Class<? extends Module> target;

    private Query(final Key<T> key, final Class<? extends Module> target) {
        Objects.requireNonNull(key, "key");
        this.key = key;
        this.target = target;
    }

    public Key<T> getKey() {
        return key;
    }

    public Class<? extends Module> getTarget() {
        return target;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Query<?> query = (Query<?>) o;
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
