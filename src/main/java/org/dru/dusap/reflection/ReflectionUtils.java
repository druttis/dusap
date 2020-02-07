package org.dru.dusap.reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ReflectionUtils {
    public static boolean isInterface(final Class<?> clazz) {
        return clazz.isInterface();
    }

    public static boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isStereotype(final Class<?> clazz) {
        return isInterface(clazz) || isAbstract(clazz);
    }

    public static boolean isConcrete(final Class<?> clazz) {
        return !isStereotype(clazz);
    }

    public static <T> List<Class<? super T>> getSuperclasses(final Class<T> subclass) {
        Objects.requireNonNull(subclass, "subclass");
        final List<Class<? super T>> superclasses = new ArrayList<>();
        Class<? super T> superclass = subclass.getSuperclass();
        while (superclass != null) {
            superclasses.add(superclass);
            superclass = superclass.getSuperclass();
        }
        Collections.reverse(superclasses);
        return superclasses;
    }

    public static <T> List<Class<? super T>> getClassHierarchy(final Class<T> targetClass) {
        final List<Class<? super T>> classHierarchy = getSuperclasses(targetClass);
        classHierarchy.add(targetClass);
        return classHierarchy;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Constructor<T>> getConstructors(final Class<T> concreteClass) {
        if (!isConcrete(concreteClass)) {
            throw new IllegalArgumentException(concreteClass + " is not concrete");
        }
        return Arrays.stream(concreteClass.getDeclaredConstructors())
                .map(constructor -> (Constructor<T>) constructor)
                .collect(Collectors.toList());
    }

    public static <T> Constructor<T> getDefaultConstructor(final Class<T> concreteClass) {
        try {
            return concreteClass.getDeclaredConstructor();
        } catch (final NoSuchMethodException exc) {
            throw new IllegalArgumentException(concreteClass + " has no default constructor");
        }
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Collection<Object> initargs) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(initargs.toArray(new Object[0]));
        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException exc) {
            throw new RuntimeException("failed to create new instance: " + constructor.toGenericString(), exc);
        }
    }

    public static <T> T newInstance(final Constructor<T> constructor) {
        return newInstance(constructor, Collections.emptyList());
    }

    public static <T> T newInstance(final Class<T> type) {
        return newInstance(getDefaultConstructor(type));
    }

    @SuppressWarnings("unchecked")
    public static <T> T copyInstance(final T source) {
        if (source == null) {
            return null;
        }
        final Class<T> objectClass = (Class<T>) source.getClass();
        final Constructor<T> constructor;
        try {
            constructor = objectClass.getDeclaredConstructor();
        } catch (final NoSuchMethodException exc) {
            return source;
        }
        if (objectClass.isPrimitive() || objectClass.equals(String.class)
                || Number.class.isAssignableFrom(objectClass)) {
            return source;
        }
        final T target = newInstance(constructor, Collections.emptyList());
        getFields(objectClass).forEach(field -> setField(target, field, copyInstance(getField(source, field))));
        return target;
    }

    public static List<Field> getFields(final Class<?> clazz) {
        return getClassHierarchy(clazz).stream()
                .flatMap(currentType -> Arrays.stream(currentType.getDeclaredFields()))
                .collect(Collectors.toList());
    }

    public static List<Field> getSerializableFields(final Class<?> clazz) {
        return getFields(clazz).stream().
                filter(field -> {
                    final int mod = field.getModifiers();
                    return !Modifier.isTransient(mod)
                            && !Modifier.isStatic(mod);
                }).collect(Collectors.toList());
    }

    public static Object getField(final Object object, final Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (final IllegalAccessException exc) {
            throw new IllegalArgumentException("could not get field value: " + field.toGenericString(), exc);
        }
    }

    public static void setField(final Object object, final Field field, final Object value) {
        try {
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (final IllegalAccessException | NoSuchFieldException exc) {
            throw new RuntimeException("failed by change field modifiers: " + field.toGenericString(), exc);
        }
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (final IllegalAccessException exc) {
            throw new IllegalArgumentException("could not set field value: " + field.toGenericString(), exc);
        }
    }

    public static List<Method> getMethods(final Class<?> targetClass) {
        return getClassHierarchy(targetClass).stream()
                .flatMap(currentType -> Arrays.stream(currentType.getDeclaredMethods()))
                .collect(Collectors.toList());
    }

    public static Object invokeMethod(final Object object, final Method method, final Collection<Object> args) {
        try {
            method.setAccessible(true);
            return method.invoke(object, args.toArray(new Object[0]));
        } catch (final IllegalAccessException exc) {
            final RuntimeException rte = new RuntimeException("failed to invoke method: " + method.toGenericString(),
                    exc);
            rte.setStackTrace(new StackTraceElement[0]);
            return rte;
        } catch (final InvocationTargetException exc) {
            final Throwable cause = exc.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            final RuntimeException rte = new RuntimeException(exc.getCause());
            rte.setStackTrace(new StackTraceElement[0]);
            exc.getCause().setStackTrace(new StackTraceElement[0]);
            throw rte;
        }
    }

    public static List<Parameter> getParameters(final Executable executable) {
        return new ArrayList<>(Arrays.asList(executable.getParameters()));
    }

    public static Class<?> box(final Class<?> type) {
        if (type == Boolean.TYPE) {
            return Boolean.class;
        }
        if (type == Byte.TYPE) {
            return Byte.class;
        }
        if (type == Character.TYPE) {
            return Character.class;
        }
        if (type == Short.TYPE) {
            return Short.class;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        }
        if (type == Long.TYPE) {
            return Long.class;
        }
        if (type == Float.TYPE) {
            return Float.class;
        }
        if (type == Double.TYPE) {
            return Double.class;
        }
        return type;
    }

    public static Class<?> unBox(final Class<?> type) {
        if (type == Boolean.class) {
            return Boolean.TYPE;
        }
        if (type == Byte.class) {
            return Byte.TYPE;
        }
        if (type == Character.class) {
            return Character.TYPE;
        }
        if (type == Short.class) {
            return Short.TYPE;
        }
        if (type == Integer.class) {
            return Integer.TYPE;
        }
        if (type == Long.class) {
            return Long.TYPE;
        }
        if (type == Float.class) {
            return Float.TYPE;
        }
        if (type == Double.class) {
            return Double.TYPE;
        }
        return type;
    }

    public static List<StackTraceElement> getStackTrace() {
        final Throwable exc = new Throwable();
        exc.fillInStackTrace();
        final List<StackTraceElement> list = new ArrayList<>(Arrays.asList(exc.getStackTrace()));
        list.remove(0);
        return list;
    }

    public static StackTraceElement getStackTrace(final int callerLevel) {
        return getStackTrace().get(callerLevel + 1);
    }

    public static <E extends Throwable> void raise(final E exc, final int callerLevel) throws E {
        final List<StackTraceElement> list = getStackTrace();
        exc.setStackTrace(list.subList(callerLevel + 1, list.size()).toArray(new StackTraceElement[0]));
        throw exc;
    }

    public static String toString(final Class<?> type) {
        return (type.getPackage().equals(Package.getPackage("java.lang")) ? type.getSimpleName() : type.getName());
    }

    private ReflectionUtils() {
    }
}
