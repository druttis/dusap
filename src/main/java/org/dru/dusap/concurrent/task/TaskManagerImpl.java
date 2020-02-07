package org.dru.dusap.concurrent.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

public final class TaskManagerImpl implements TaskManager {
    private final TaskExecutorFactory executorFactory;
    private final Map<String, TaskExecutor> executorByName;

    public TaskManagerImpl(final TaskExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        executorByName = new ConcurrentHashMap<>();
    }

    @Override
    public TaskExecutor newExecutor(final String name, final ThreadFactory threadFactory) {
        return addExecutor(name, () -> executorFactory.newExecutor(name, threadFactory));
    }

    @Override
    public TaskExecutor newExecutor(final String name) {
        return addExecutor(name, () -> executorFactory.newExecutor(name));
    }

    @Override
    public TaskExecutor newExecutor(final String name, final int numThreads, final ThreadFactory threadFactory) {
        return addExecutor(name, () -> executorFactory.newExecutor(name, numThreads, threadFactory));
    }

    @Override
    public TaskExecutor newExecutor(final String name, final int numThreads) {
        return addExecutor(name, () -> executorFactory.newExecutor(name, numThreads));
    }

    @Override
    public TaskExecutor getExecutor(final String name) {
        final TaskExecutor executor = executorByName.get(name);
        if (executor == null) {
            throw new IllegalStateException(name + " does not exist");
        }
        return executor;
    }

    private TaskExecutor addExecutor(final String name, final Supplier<TaskExecutor> supplier) {
        return executorByName.compute(name, ($, existing) -> {
            if (existing != null) {
                throw new IllegalStateException(name + " already exist");
            }
            return supplier.get();
        });
    }
}
