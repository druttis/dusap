package org.dru.dusap.concurrent.task;

import org.dru.dusap.event.EventBus;
import org.dru.dusap.time.TimeSupplier;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class TaskExecutorFactoryImpl implements TaskExecutorFactory {
    private final EventBus eventBus;
    private final TimeSupplier timeSupplier;

    public TaskExecutorFactoryImpl(final EventBus eventBus, final TimeSupplier timeSupplier) {
        this.eventBus = eventBus;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public TaskExecutor newExecutor(final String name, final ThreadFactory threadFactory) {
        return new TaskExecutorImpl(eventBus, timeSupplier, Executors.newSingleThreadScheduledExecutor(threadFactory), name);
    }

    @Override
    public TaskExecutor newExecutor(final String name) {
        return new TaskExecutorImpl(eventBus, timeSupplier, Executors.newSingleThreadScheduledExecutor(), name);
    }

    @Override
    public TaskExecutor newExecutor(final String name, final int numThreads, final ThreadFactory threadFactory) {
        if (numThreads == 1) {
            return newExecutor(name, threadFactory);
        }
        return new TaskExecutorImpl(eventBus, timeSupplier, Executors.newScheduledThreadPool(numThreads, threadFactory), name);
    }

    @Override
    public TaskExecutor newExecutor(final String name, final int numThreads) {
        if (numThreads == 1) {
            return newExecutor(name);
        }
        return new TaskExecutorImpl(eventBus, timeSupplier, Executors.newScheduledThreadPool(numThreads), name);
    }
}
