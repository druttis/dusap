package org.dru.dusap.rpc.json;

import org.dru.dusap.concurrent.ConcurrentModule;
import org.dru.dusap.concurrent.task.TaskManager;
import org.dru.dusap.event.EventModule;
import org.dru.dusap.injection.DependsOn;
import org.dru.dusap.injection.Inject;
import org.dru.dusap.injection.Module;
import org.dru.dusap.json.JsonModule;
import org.dru.dusap.rpc.RpcClientManager;
import org.dru.dusap.rpc.RpcModule;
import org.dru.dusap.time.TimeModule;

@DependsOn({ConcurrentModule.class, EventModule.class, JsonModule.class, RpcModule.class, TimeModule.class})
public final class JsonRpcModule extends Module {
    @Override
    protected void configure() {
        bind(JsonRpcClientFactory.class).toType(JsonRpcClientFactoryImpl.class).asSingleton();
        bind(JsonRpcMessageFactory.class).toType(JsonRpcMessageFactoryImpl.class).asSingleton();
    }

    @Inject
    private void register(final RpcClientManager manager, final JsonRpcClientFactory factory) {
        manager.register("jsonrpc", factory);
    }
}
