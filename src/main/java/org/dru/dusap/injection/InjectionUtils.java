package org.dru.dusap.injection;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public final class InjectionUtils {
    public static boolean isInjectAnnotated(final AnnotatedElement element) {
        return element.isAnnotationPresent(Inject.class);
    }

    public static <T> List<Constructor<T>> getInjectAnnotatedConstructors(final Class<T> type) {
        return ReflectionUtils.getConstructors(type).stream()
                .filter(InjectionUtils::isInjectAnnotated)
                .collect(Collectors.toList());
    }

    public static <T> Constructor<T> getInjectableConstructor(final Class<T> type) {
        final List<Constructor<T>> constructors = ReflectionUtils.getConstructors(type);
        if (constructors.isEmpty()) {
            throw new IllegalArgumentException(type.getName() + " has no constructor");
        } else if (constructors.size() == 1) {
            return constructors.get(0);
        } else {
            final List<Constructor<T>> injectAnnotatedConstructors = getInjectAnnotatedConstructors(type);
            if (injectAnnotatedConstructors.isEmpty()) {
                throw new IllegalArgumentException(type.getName()
                        + " has several constructors of which none is @Inject annotated");
            } else if (injectAnnotatedConstructors.size() == 1) {
                return injectAnnotatedConstructors.get(0);
            } else {
                throw new IllegalArgumentException(type.getName()
                        + " has several constructors that are @Inject annotated");
            }
        }
    }

    public static List<Field> getInjectAnnotatedFields(final Class<?> type) {
        return ReflectionUtils.getFields(type).stream()
                .filter(InjectionUtils::isInjectAnnotated).collect(Collectors.toList());
    }

    public static List<Method> getInjectAnnotatedMethods(final Class<?> type) {
        return ReflectionUtils.getMethods(type).stream()
                .filter(InjectionUtils::isInjectAnnotated).collect(Collectors.toList());
    }

    private InjectionUtils() {
    }
}
