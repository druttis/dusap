package org.dru.dusap.reflection;

public class ReflectionException extends RuntimeException {
    public ReflectionException() {
    }

    public ReflectionException(final String message) {
        super(message);
    }

    public ReflectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(final Throwable cause) {
        super(cause);
    }
}
