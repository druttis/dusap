package org.dru.dusap.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class ThrottlingInvocationHandler implements InvocationHandler {
    public static <T> T of(final Class<? extends T> interfaceClass, final T object, final long latencyMs) {
        Objects.requireNonNull(interfaceClass, "interfaceClass");
        final ClassLoader classLoader = interfaceClass.getClassLoader();
        final Class<?>[] interfaceClasses = {interfaceClass};
        final ThrottlingInvocationHandler invocationHandler = new ThrottlingInvocationHandler(object, latencyMs);
        final Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaceClasses, invocationHandler);
        return interfaceClass.cast(proxyInstance);
    }

    private final Object object;
    private final long latencyMs;

    private ThrottlingInvocationHandler(final Object object, final long latencyMs) {
        Objects.requireNonNull(object, "object");
        if (latencyMs < 0) {
            throw new IllegalArgumentException("Negative latencyMs: " + latencyMs);
        }
        this.object = object;
        this.latencyMs = latencyMs;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Thread.sleep(latencyMs);
        return method.invoke(object, args);
    }
}
