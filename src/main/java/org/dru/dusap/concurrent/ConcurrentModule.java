package org.dru.dusap.concurrent;

import org.dru.dusap.concurrent.task.TaskExecutorFactory;
import org.dru.dusap.concurrent.task.TaskExecutorFactoryImpl;
import org.dru.dusap.concurrent.task.TaskManager;
import org.dru.dusap.concurrent.task.TaskManagerImpl;
import org.dru.dusap.event.EventModule;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Module;
import org.dru.dusap.time.TimeModule;

@DependsOn({EventModule.class, TimeModule.class})
public final class ConcurrentModule extends Module {
    public ConcurrentModule() {
    }

    @Override
    protected void configure() {
        bind(TaskExecutorFactory.class).toType(TaskExecutorFactoryImpl.class).asSingleton();
        bind(TaskManager.class).toType(TaskManagerImpl.class).asSingleton();
        expose(TaskExecutorFactory.class);
        expose(TaskManager.class);
    }
}
