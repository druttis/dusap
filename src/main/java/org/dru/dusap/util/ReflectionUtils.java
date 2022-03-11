package org.dru.dusap.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;

public final class ReflectionUtils {
    @SuppressWarnings("unchecked")
    public static <T> Stream<Constructor<T>> getDeclaredConstructors(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        return Stream.of(type.getDeclaredConstructors()).map(constructor -> (Constructor<T>) constructor);
    }

    public static <T> Constructor<T> getDeclaredDefaultConstructor(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        return getDeclaredConstructors(type)
                .filter(constructor -> constructor.getParameterTypes().length == 0)
                .findFirst()
                .orElseThrow(() -> new ReflectionException("No declared default constructor found: type=" + type));
    }

    private static boolean match(final Parameter[] params, final Object[] args) {
        if (params.length != args.length) {
            return false;
        }
        for (int index = 0; index < params.length; index++) {
            if (args[index] != null && !params[index].getType().isInstance(args[index])) {
                return false;
            }
        }
        return true;
    }

    public static <T> Stream<Class<? super T>> getClassHierarchy(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        final List<Class<? super T>> result = new ArrayList<>();
        Class<? super T> current = type;
        while (current != null) {
            result.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(result);
        return result.stream();
    }

    public static Stream<Field> getDeclaredFields(final Class<?> type) {
        return getClassHierarchy(type).flatMap(current -> Stream.of(current.getDeclaredFields()));
    }

    public static Stream<Method> getDeclaredMethods(final Class<?> type) {
        return getClassHierarchy(type).flatMap(current -> Stream.of(current.getDeclaredMethods()));
    }

    public static Stream<Parameter> getParameters(final Executable executable) {
        Objects.requireNonNull(executable, "executable");
        return Stream.of(executable.getParameters());
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Stream<?> initargs) {
        Objects.requireNonNull(constructor, "constructor");
        Objects.requireNonNull(initargs, "initargs");
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(initargs.toArray());
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException exc) {
            throw new ReflectionException("Failed to create new instance: " + constructor.toGenericString(), exc);
        }
    }

    public static <T> T newInstance(final Constructor<T> constructor) {
        return newInstance(constructor, Stream.empty());
    }

    public static Object getFieldValue(final Object instance, final Field field) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(field, "field");
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (final IllegalAccessException exc) {
            throw new ReflectionException("Failed to get field value: " + field.toGenericString(), exc);
        }
    }

    public static void setFieldValue(final Object instance, final Field field, final Object value) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(field, "field");
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (final IllegalAccessException exc) {
            throw new ReflectionException("Failed to set field value: " + field.toGenericString(), exc);
        }
    }

    public static Object invokeMethod(final Object instance, final Method method, final Stream<Object> args) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(args, "args");
        method.setAccessible(true);
        try {
            final Object[] values = args.toArray();
            return method.invoke(instance, values);
        } catch (final IllegalAccessException | InvocationTargetException exc) {
            throw new ReflectionException("Failed to invoke method: " + method.toGenericString(), exc);
        }
    }

    public static Object invokeMethod(final Object instance, final Method method) {
        return invokeMethod(instance, method, Stream.empty());
    }

    @SuppressWarnings("unchecked")
    public static <T> T copyInstance(final T source) {
        if (source == null) {
            return null;
        }
        final Class<T> type = (Class<T>) source.getClass();
        if (type.isPrimitive() || type.equals(String.class)
                || Number.class.isAssignableFrom(type)) {
            return source;
        }
        final Constructor<T> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (final NoSuchMethodException exc) {
            throw new ReflectionException("No default constructor: " + type.toGenericString());
        }
        final T target = newInstance(constructor);
        getDeclaredFields(type).filter(field -> {
            final int mod = field.getModifiers();
            return !Modifier.isStatic(mod);
        }).forEach(field -> setFieldValue(target, field, copyInstance(getFieldValue(source, field))));
        return target;
    }

    public static Stream<? extends Annotation> getAnnotations(final AnnotatedElement element) {
        Objects.requireNonNull(element, "element");
        return Stream.of(element.getAnnotations());
    }

    public static Stream<? extends Annotation> getAnnotatedAnnotations(
            final AnnotatedElement element, final Class<? extends Annotation> annotationType) {
        Objects.requireNonNull(annotationType, "annotationType");
        return getAnnotations(element)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(annotationType));
    }

    public static Annotation getAnnotatedAnnotationOrNull(
            final AnnotatedElement element, final Class<? extends Annotation> annotationType) {
        Objects.requireNonNull(annotationType, "annotationType");
        final List<? extends Annotation> annotations = getAnnotatedAnnotations(element, annotationType)
                .collect(Collectors.toList());
        if (annotations.size() > 1) {
            throw new IllegalArgumentException("Multiple " + annotationType.getName() + " annotated annotations:" +
                    element.getClass());
        }
        return annotations.isEmpty() ? null : annotations.get(0);
    }

    public static int hashCode(final Class<? extends Annotation> annotationType, final Map<Method, Object> members) {
        return getDeclaredMethods(annotationType)
                .mapToInt(method -> hashCode(method, members))
                .sum();
    }

    public static int hashCode(final Method method, final Map<Method, Object> members) {
        return (127 * method.getName().hashCode()) ^ (deepHashCode(asArray(members.get(method))) - 31);
    }

    public static boolean equals(final Class<? extends Annotation> annotationType, final Map<Method, Object> members,
                                 final Object other) {
        if (!annotationType.isInstance(other)) {
            return false;
        }
        return getDeclaredMethods(annotationType)
                .allMatch(method -> deepEquals(asArray(invokeMethod(other, method)), asArray(members.get(method))));
    }

    public static String toString(final Class<? extends Annotation> annotationType, final Map<Method, Object> members) {
        final StringBuilder sb = new StringBuilder("@").append(annotationType.getName()).append('(');
        sb.append(getDeclaredMethods(annotationType)
                .map(method -> {
                    final String value = Arrays.deepToString(asArray(members.get(method)));
                    return String.format("%s=%s", method.getName(), value.substring(1, value.length() - 1));
                })
                .collect(Collectors.joining(", "))
        );
        return sb.append(')').toString();
    }

    public static <E extends Enum<E>> Stream<E> getEnumConstants(final Class<E> type) {
        Objects.requireNonNull(type, "type");
        if (!type.isEnum()) {
            throw new IllegalArgumentException("Not an enum: " + type.toGenericString());
        }
        final List<E> values = new ArrayList<>();
        for (final Field field : type.getFields()) {
            if (field.isEnumConstant()) {
                try {
                    values.add(type.cast(field.get(null)));
                } catch (final IllegalAccessException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
        return values.stream();
    }

    public static Object[] asArray(final Object value) {
        return Stream.of(value).toArray();
    }

    private ReflectionUtils() {
    }
}
