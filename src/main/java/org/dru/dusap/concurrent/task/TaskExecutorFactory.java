package org.dru.dusap.concurrent.task;

import java.util.concurrent.ThreadFactory;

public interface TaskExecutorFactory {
    TaskExecutor newExecutor(String name, ThreadFactory threadFactory);

    TaskExecutor newExecutor(String name);

    TaskExecutor newExecutor(String name, int numThreads, ThreadFactory threadFactory);

    TaskExecutor newExecutor(String name, int numThreads);
}
