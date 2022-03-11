package org.dru.dusap.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class AnnotationProxy extends HashMap<Method, Object> implements Annotation, InvocationHandler {
    private final Class<? extends Annotation> annotationType;
    private final int hashCode;

    AnnotationProxy(final Class<? extends Annotation> annotationType, final Map<? extends Method, ?> m) {
        super(m);
        this.annotationType = annotationType;
        hashCode = ReflectionUtils.hashCode(annotationType, this);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        final Object value = get(method);
        if (value != null) {
            return value;
        }
        final String name = method.getName();
        switch (name) {
            case "annotationType":
                return annotationType;
            case "toString":
                return ReflectionUtils.toString(annotationType, this);
            case "hashCode":
                return hashCode;
            case "equals":
                return ReflectionUtils.equals(annotationType, this, args[0]);
            default:
                throw new ReflectionException("No such method: " + name);
        }
    }
}
