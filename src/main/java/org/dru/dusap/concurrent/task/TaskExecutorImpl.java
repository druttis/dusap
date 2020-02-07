package org.dru.dusap.concurrent.task;

import org.dru.dusap.event.EventBus;
import org.dru.dusap.time.TimeSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

final class TaskExecutorImpl implements TaskExecutor {
    private final EventBus eventBus;
    private final TimeSupplier timeSupplier;
    private final ScheduledExecutorService service;
    private final String name;
    private final AtomicLong taskIdCounter;

    TaskExecutorImpl(final EventBus eventBus, final TimeSupplier timeSupplier, final ScheduledExecutorService service,
                     final String name) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.timeSupplier = Objects.requireNonNull(timeSupplier, "timeSupplier");
        this.service = Objects.requireNonNull(service, "service");
        this.name = Objects.requireNonNull(name, "name");
        taskIdCounter = new AtomicLong();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Task<?> schedule(final String name, final Runnable command, final Instant when) {
        return schedule(name, command, until(when));
    }

    @Override
    public Task<?> schedule(final String name, final Runnable command, final Duration delay) {
        final TaskImpl<?> task = newTask(name);
        final Future future = service.schedule(wrapCommand(command, task), delay.toMillis(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task<?> submit(final String name, final Runnable command) {
        return schedule(name, command, Duration.ZERO);
    }

    @Override
    public <T> Task<T> schedule(final String name, final Runnable command, final T result, final Instant when) {
        return schedule(name, command, result, until(when));
    }

    @Override
    public <T> Task<T> schedule(final String name, final Runnable command, final T result, final Duration delay) {
        final TaskImpl<T> task = newTask(name);
        final Future<T> future = service.schedule(wrapCommand(command, task, result), delay.toMillis(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public <T> Task<T> submit(final String name, final Runnable command, final T result) {
        return schedule(name, command, result, Duration.ZERO);
    }

    @Override
    public <T> Task<T> schedule(final String name, final Callable<? extends T> callable, final Instant when) {
        return schedule(name, callable, until(when));
    }

    @Override
    public <T> Task<T> schedule(final String name, final Callable<? extends T> callable, final Duration delay) {
        final TaskImpl<T> task = newTask(name);
        final Future<T> future = service.schedule(wrapCallable(callable, task), delay.toMillis(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public <T> Task<T> submit(final String name, final Callable<? extends T> callable) {
        return schedule(name, callable, Duration.ZERO);
    }

    @Override
    public Task<?> scheduleWithFixedDelay(final String name, final Runnable command, final Instant when,
                                          final Duration delay) {
        return scheduleWithFixedDelay(name, command, until(when), delay);
    }

    @Override
    public Task<?> scheduleWithFixedDelay(final String name, final Runnable command, final Duration initialDelay,
                                          final Duration delay) {
        final TaskImpl<?> task = newTask(name);
        final Future future = service.scheduleWithFixedDelay(wrapCommand(command, task), initialDelay.toMillis(),
                delay.toMillis(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task<?> submitWithFixedDelay(final String name, final Runnable command, final Duration delay) {
        return scheduleWithFixedDelay(name, command, Duration.ZERO, delay);
    }

    @Override
    public Task<?> scheduleWithFixedRate(final String name, final Runnable command, final Instant when,
                                         final Duration rate) {
        return scheduleWithFixedRate(name, command, until(when), rate);
    }

    @Override
    public Task<?> scheduleWithFixedRate(final String name, final Runnable command, final Duration initialDelay,
                                         final Duration rate) {
        final TaskImpl<?> task = newTask(name);
        final Future future = service.scheduleAtFixedRate(wrapCommand(command, task), initialDelay.toMillis(),
                rate.toMillis(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
        return task;
    }

    @Override
    public Task<?> submitWithFixedRate(final String name, final Runnable command, final Duration rate) {
        return scheduleWithFixedRate(name, command, Duration.ZERO, rate);
    }

    private Instant now() {
        return timeSupplier.get();
    }

    private Duration until(final Instant when) {
        final Instant now = now();
        return now.isAfter(when) ? Duration.ZERO : Duration.between(now, when);
    }

    private <T> TaskImpl<T> newTask(final String name) {
        return new TaskImpl<>(eventBus, timeSupplier, taskIdCounter.incrementAndGet(), name);
    }

    private <T> Runnable wrapCommand(final Runnable command, final TaskImpl<T> task) {
        return () -> {
            command.run();
            task.incrementCount();
        };
    }

    private <T> Callable<T> wrapCommand(final Runnable command, final TaskImpl<T> task, final T result) {
        return Executors.callable(wrapCommand(command, task), result);
    }

    private <T> Callable<T> wrapCallable(final Callable<? extends T> callable, final TaskImpl<T> task) {
        return () -> {
            final T result = callable.call();
            task.incrementCount();
            return result;
        };
    }
}
