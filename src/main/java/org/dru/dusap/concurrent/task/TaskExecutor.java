package org.dru.dusap.concurrent.task;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public interface TaskExecutor {
    String getName();

    Task<?> schedule(String name, Runnable command, Instant when);

    Task<?> schedule(String name, Runnable command, Duration delay);

    Task<?> submit(String name, Runnable command);

    <T> Task<T> schedule(String name, Runnable command, T result, Instant when);

    <T> Task<T> schedule(String name, Runnable command, T result, Duration delay);

    <T> Task<T> submit(String name, Runnable command, T result);

    <T> Task<T> schedule(String name, Callable<? extends T> callable, Instant when);

    <T> Task<T> schedule(String name, Callable<? extends T> callable, Duration delay);

    <T> Task<T> submit(String name, Callable<? extends T> callable);

    Task<?> scheduleWithFixedDelay(String name, Runnable command, Instant when, Duration delay);

    Task<?> scheduleWithFixedDelay(String name, Runnable command, Duration initialDelay, Duration delay);

    Task<?> submitWithFixedDelay(String name, Runnable command, Duration delay);

    Task<?> scheduleWithFixedRate(String name, Runnable command, Instant when, Duration rate);

    Task<?> scheduleWithFixedRate(String name, Runnable command, Duration initialDelay, Duration rate);

    Task<?> submitWithFixedRate(String name, Runnable command, Duration rate);
}
