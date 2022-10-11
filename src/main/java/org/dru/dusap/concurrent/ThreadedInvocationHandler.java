package org.dru.dusap.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public final class ThreadedInvocationHandler implements InvocationHandler {
    private final InvocationHandler target;
    private final ExecutorService executor;

    public ThreadedInvocationHandler(final InvocationHandler target, final ExecutorService executor) {
        this.target = target;
        this.executor = executor;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final AtomicReference<Throwable> exc = new AtomicReference<>();
        final Object result = executor.submit(() -> {
            try {
                return target.invoke(proxy, method, args);
            } catch (Throwable e) {
                exc.set(e);
                return null;
            }
        }).get();
        if (exc.get() != null) {
            throw exc.get();
        }
        return result;
    }
}
