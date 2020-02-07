package org.dru.dusap.rpc;

import org.dru.dusap.concurrent.ConcurrentModule;
import org.dru.dusap.concurrent.task.TaskManager;
import org.dru.dusap.event.EventModule;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Inject;
import org.dru.dusap.injection.Module;
import org.dru.dusap.time.TimeModule;

@DependsOn({ConcurrentModule.class, EventModule.class, TimeModule.class})
public final class RpcModule extends Module {
    public RpcModule() {
    }

    @Override
    protected void configure() {
        bind(RpcClientManager.class).toType(RpcClientManagerImpl.class).asSingleton();
        expose(RpcClientManager.class);
    }

    @Inject
    private void createRpcExecutor(final TaskManager taskManager) {
        // TODO: number of threads should be configurable
        taskManager.newExecutor("rpc", 4);
    }
}
