package org.dru.dusap.inject.provider;

import org.dru.dusap.inject.Injector;

import javax.inject.Provider;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ProvidesProvider<T> implements Provider<T> {
    private final Method method;
    private final Object instance;
    private final Injector injector;

    public ProvidesProvider(final Method method, final Object instance, final Injector injector) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(injector, "injector");
        this.method = method;
        this.instance = instance;
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        return (T) injector.injectMethod(instance, method);
    }
}
