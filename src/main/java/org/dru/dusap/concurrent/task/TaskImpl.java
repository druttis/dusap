package org.dru.dusap.concurrent.task;

import org.dru.dusap.event.EventBus;
import org.dru.dusap.event.EventDispatcherSupport;
import org.dru.dusap.time.TimeSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class TaskImpl<T> extends EventDispatcherSupport implements Task<T> {
    private final TimeSupplier timeSupplier;
    private final long id;
    private final String name;
    private final AtomicReference<TaskState> state;
    private final AtomicLong count;
    private Future<T> future;

    TaskImpl(final EventBus eventBus, final TimeSupplier timeSupplier, final long id, final String name) {
        super(eventBus);
        this.timeSupplier = timeSupplier;
        this.id = id;
        this.name = name;
        state = new AtomicReference<>(TaskState.CREATED);
        count = new AtomicLong();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void cancel(final boolean mayInterruptIfRunning) {
        future.cancel(mayInterruptIfRunning);
    }

    @Override
    public TaskState getState() {
        return state.get();
    }

    @Override
    public long getCount() {
        return count.get();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public T get(final Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    @SuppressWarnings("unchecked")
    void setFuture(final Future future) {
        this.future = future;
    }

    void incrementCount() {
        count.incrementAndGet();
        fireEvent(new TaskStateChangeEvent(this));
    }

    private Instant now() {
        return timeSupplier.get();
    }
}
