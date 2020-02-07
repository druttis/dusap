package org.dru.dusap.concurrent.task;

public interface TaskManager extends TaskExecutorFactory {
    TaskExecutor getExecutor(String name);
}
