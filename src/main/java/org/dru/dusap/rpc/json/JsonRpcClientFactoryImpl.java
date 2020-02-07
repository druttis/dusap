package org.dru.dusap.rpc.json;

import org.dru.dusap.concurrent.task.TaskExecutor;
import org.dru.dusap.concurrent.task.TaskManager;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.json.JsonSerializerSupplier;
import org.dru.dusap.rpc.RpcClient;
import org.dru.dusap.time.TimeSupplier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class JsonRpcClientFactoryImpl implements JsonRpcClientFactory {
    private final EventBus eventBus;
    private final JsonSerializer serializer;
    private final JsonRpcMessageFactory factory;
    private final TimeSupplier timeSupplier;
    private final TaskExecutor taskExecutor;

    public JsonRpcClientFactoryImpl(final EventBus eventBus, final JsonSerializerSupplier serializerSupplier,
                                    final JsonRpcMessageFactory factory, final TimeSupplier timeSupplier,
                                    final TaskManager taskManager) {
        this.eventBus = eventBus;
        serializer = serializerSupplier.get();
        this.factory = factory;
        this.timeSupplier = timeSupplier;
        taskExecutor = taskManager.getExecutor("rpc");
    }

    @Override
    public RpcClient newClient(final String url) {
        Objects.requireNonNull(url, "url");
        try {
            return new JsonRpcClient(eventBus, serializer, factory, timeSupplier, taskExecutor, new URL(url));
        } catch (final MalformedURLException exc) {
            throw new IllegalArgumentException(exc);
        }
    }
}
