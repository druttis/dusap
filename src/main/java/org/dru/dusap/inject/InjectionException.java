package org.dru.dusap.inject;

public class InjectionException extends RuntimeException{
    public InjectionException() {
    }

    public InjectionException(final String message) {
        super(message);
    }

    public InjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InjectionException(final Throwable cause) {
        super(cause);
    }
}
