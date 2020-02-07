package org.dru.dusap.rpc.json;

import org.dru.dusap.concurrent.task.TaskExecutor;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.rpc.RpcClient;
import org.dru.dusap.rpc.RpcToken;
import org.dru.dusap.time.TimeSupplier;

import java.net.URL;

public final class JsonRpcClient implements RpcClient {
    private final EventBus eventBus;
    private final JsonSerializer serializer;
    private final JsonRpcMessageFactory factory;
    private final TimeSupplier timeSupplier;
    private final TaskExecutor executor;
    private final URL url;
    private final Object monitor;
    private volatile JsonRpcBatch batch;

    public JsonRpcClient(final EventBus eventBus, final JsonSerializer serializer, final JsonRpcMessageFactory factory,
                         final TimeSupplier timeSupplier, final TaskExecutor executor, final URL url) {
        this.eventBus = eventBus;
        this.serializer = serializer;
        this.factory = factory;
        this.timeSupplier = timeSupplier;
        this.executor = executor;
        this.url = url;
        monitor = new Object();
        newBatch();
    }

    @Override
    public <T> RpcToken<T> request(final String method, final Class<T> returnType, final Object... params) {
        synchronized (monitor) {
            return batch.request(method, returnType, params);
        }
    }

    @Override
    public RpcToken<?> notify(final String method, final Object... params) {
        synchronized (monitor) {
            return batch.notify(method, params);
        }
    }

    void newBatch() {
        batch = new JsonRpcBatch(eventBus, serializer, factory, timeSupplier, this::newBatch, executor, url);
    }
}
