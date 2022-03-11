package org.dru.dusap.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AnnotationBuilder<A extends Annotation> implements Builder<A> {
    private final Class<A> annotationType;
    private final Map<Method, Object> members;

    AnnotationBuilder(final Class<A> annotationType) {
        Objects.requireNonNull(annotationType, "annotationType");
        this.annotationType = annotationType;
        members = new ConcurrentHashMap<>();
    }

    @Override
    public A build() {
        ReflectionUtils.getDeclaredMethods(annotationType)
                .filter(method -> !members.containsKey(method))
                .filter(method -> method.getDefaultValue() != null)
                .forEach(method -> members.put(method, method.getDefaultValue()));
        final Set<String> missing = ReflectionUtils.getDeclaredMethods(annotationType)
                .filter(method -> !members.containsKey(method))
                .map(Method::getName)
                .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new RuntimeException("Missing members: " + missing);
        }
        try {
            return annotationType.cast(Proxy.newProxyInstance(annotationType.getClassLoader(),
                    Stream.of(annotationType).toArray(Class[]::new), new AnnotationProxy(annotationType, members)));
        } catch (final Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public Class<A> annotationType() {
        return annotationType;
    }

    public Builder<A> with(final String name, final Object value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        final Method method;
        try {
            method = annotationType.getDeclaredMethod(name);
        } catch (final NoSuchMethodException exc) {
            throw new ReflectionException("No such member: " + name);
        }
        final Class<?> returnType = method.getReturnType();
        if (!returnType.isInstance(value)) {
            throw new IllegalArgumentException(String.format("%s is not an instance of %s", value, returnType));
        }
        if (members.containsKey(method)) {
            throw new IllegalArgumentException(String.format("%s already a member", name));
        }
        members.put(method, value);
        return this;
    }

    public Builder<A> with(final Object value) {
        return with("value", value);
    }
}
