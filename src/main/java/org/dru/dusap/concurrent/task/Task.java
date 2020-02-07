package org.dru.dusap.concurrent.task;

import org.dru.dusap.event.EventDispatcher;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface Task<T> extends EventDispatcher {
    long getId();

    String getName();

    void cancel(boolean mayInterruptIfRunning);

    TaskState getState();

    long getCount();

    T get() throws InterruptedException, ExecutionException;

    T get(Duration timeout) throws InterruptedException, ExecutionException, TimeoutException;
}
