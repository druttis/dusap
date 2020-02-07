package org.dru.dusap.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Objects;

public final class Key<T> {
    public static <T> Key<T> of(final Class<T> type) {
        return new Key<>(type, null);
    }

    public static Key<?> of(final Parameter parameter) {
        final Key<?> key = Key.of(parameter.getType());
        final Named named = parameter.getAnnotation(Named.class);
        return (named != null ? key.named(named.value()) : key);
    }

    public static Key<?> of(final Field field) {
        final Key<?> key = Key.of(field.getType());
        final Named named = field.getAnnotation(Named.class);
        return (named != null ? key.named(named.value()) : key);
    }

    private final Class<T> type;
    private final String name;

    private Key(final Class<T> type, final String name) {
        this.type = Objects.requireNonNull(type, "type");
        this.name = name;
    }

    public Class<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Key<T> named(final String name) {
        if (this.name != null) {
            throw new IllegalStateException("name already specified");
        }
        return new Key<>(type, Objects.requireNonNull(name, "name"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        final Key<?> key = (Key<?>) o;
        return getType().equals(key.getType()) &&
                Objects.equals(getName(), key.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getName());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString(getType()));
        if (getName() != null) {
            sb.append(" named '").append(getName()).append('\'');
        }
        return sb.toString();
    }

    public static String toString(final Class<?> type) {
        return (type.getPackage().equals(Package.getPackage("java.lang")) ? type.getSimpleName() : type.getName());
    }
}
