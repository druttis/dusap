package org.dru.dusap.injection;

public final class InjectionException extends RuntimeException {
    public InjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
