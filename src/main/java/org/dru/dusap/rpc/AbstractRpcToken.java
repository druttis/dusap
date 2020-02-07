package org.dru.dusap.rpc;

import org.dru.dusap.event.EventBus;
import org.dru.dusap.event.EventDispatcherSupport;
import org.dru.dusap.rpc.event.RpcErrorEvent;
import org.dru.dusap.rpc.event.RpcResultEvent;
import org.dru.dusap.time.TimeSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractRpcToken<T> extends EventDispatcherSupport implements RpcToken<T> {
    private final TimeSupplier timeSupplier;
    private final String method;
    private final Object[] params;
    private final Class<T> returnType;
    private final Object monitor;
    private volatile int observerCount;
    private final AtomicReference<Response<T>> responseRef;

    protected AbstractRpcToken(final EventBus eventBus, final TimeSupplier timeSupplier, final String method,
                               final Object[] params, final Class<T> returnType) {
        super(eventBus);
        this.timeSupplier = Objects.requireNonNull(timeSupplier, "timeSupplier");
        this.method = Objects.requireNonNull(method, "method");
        this.params = Objects.requireNonNull(params, "parameters");
        this.returnType = Objects.requireNonNull(returnType, "returnType");
        monitor = new Object();
        responseRef = new AtomicReference<>();
    }

    @Override
    public final String getMethod() {
        return method;
    }

    @Override
    public final Object[] getParams() {
        return params;
    }

    @Override
    public final Class<T> getReturnType() {
        return returnType;
    }

    @Override
    public final T get() throws RpcError {
        try {
            while (responseRef.get() == null) {
                synchronized (monitor) {
                    observerCount++;
                    try {
                        monitor.wait();
                    } finally {
                        observerCount--;
                    }
                }
            }
            return respond();
        } catch (final InterruptedException exc) {
            throw new RpcError("interrupted", exc);
        }
    }

    @Override
    public final T get(final Duration timeout) throws RpcError {
        try {
            final Instant expire = timeSupplier.get().plus(timeout);
            while (responseRef.get() == null) {
                final Instant now = timeSupplier.get();
                final Duration duration = Duration.between(now, expire);
                if (duration.isNegative() || duration.isZero()) {
                    throw new TimeoutException();
                }
                synchronized (monitor) {
                    observerCount++;
                    try {
                        monitor.wait(duration.toMillis());
                    } finally {
                        observerCount--;
                    }
                }
            }
            return respond();
        } catch (final InterruptedException exc) {
            throw new RpcError("interrupted", exc);
        } catch (final TimeoutException exc) {
            throw new RpcError("timeout", exc);
        }
    }

    private final void setResponse(final Response<T> response) {
        Objects.requireNonNull(response, "response");
        if (!responseRef.compareAndSet(null, response)) {
            throw new IllegalStateException("response already set");
        }
        synchronized (monitor) {
            if (observerCount > 0) {
                if (observerCount > 1) {
                    monitor.notifyAll();
                } else {
                    monitor.notify();
                }
            }
        }
        if (response.getResult() != null) {
            fireEvent(new RpcResultEvent<>(this, response.getResult()));
        } else if (response.getError() != null) {
            fireEvent(new RpcErrorEvent<>(this, response.getError()));
        }
    }

    public final void setResult(final Object result) {
        setResponse(new Response<>(returnType.cast(result), null));
    }

    public final void setError(final Error error) {
        Objects.requireNonNull(error, "error");
        setResponse(new Response<>(null, error));
    }

    private T respond() {
        final Response<T> response = responseRef.get();
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getResult();
    }

    private static final class Response<T> {
        private final T result;
        private final Error error;

        private Response(final T result, final Error error) {
            this.result = result;
            this.error = error;
        }

        private T getResult() {
            return result;
        }

        private Error getError() {
            return error;
        }
    }
}
