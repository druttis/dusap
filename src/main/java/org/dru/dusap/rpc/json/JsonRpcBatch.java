package org.dru.dusap.rpc.json;

import org.dru.dusap.concurrent.task.TaskExecutor;
import org.dru.dusap.event.EventBus;
import org.dru.dusap.io.OutputInputStream;
import org.dru.dusap.json.JsonElement;
import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.rpc.RpcClient;
import org.dru.dusap.rpc.RpcToken;
import org.dru.dusap.time.TimeSupplier;
import org.dru.dusap.util.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class JsonRpcBatch implements RpcClient {
    private final EventBus eventBus;
    private final JsonSerializer serializer;
    private final JsonRpcMessageFactory factory;
    private final TimeSupplier timeSupplier;
    private final Runnable callback;
    private final TaskExecutor executor;
    private final URL url;
    private final AtomicLong idCounter;
    private final Map<Long, JsonRpcToken<?>> pending;
    private final JsonElement outgoing;
    private volatile boolean cancelled;

    public JsonRpcBatch(final EventBus eventBus, final JsonSerializer serializer, final JsonRpcMessageFactory factory,
                        final TimeSupplier timeSupplier, final Runnable callback, final TaskExecutor executor,
                        final URL url) {
        this.eventBus = eventBus;
        this.serializer = serializer;
        this.factory = factory;
        this.timeSupplier = timeSupplier;
        this.callback = callback;
        this.executor = executor;
        this.url = url;
        idCounter = new AtomicLong();
        pending = new HashMap<>();
        outgoing = serializer.newArray();
    }

    @Override
    public <T> RpcToken<T> request(final String method, final Class<T> returnType, final Object... params) {
        final long id = idCounter.getAndIncrement();
        final JsonElement message = factory.newRequestMessage(id, method, params);
        final JsonRpcToken<T> token = new JsonRpcToken<>(eventBus, timeSupplier, method, params, returnType);
        final boolean send;
        pending.put(id, token);
        enqueue(message);
        return token;
    }

    @Override
    public RpcToken<?> notify(final String method, final Object... params) {
        final JsonElement message = factory.newNotifyMessage(method, params);
        final JsonRpcToken<?> token = new JsonRpcToken<>(eventBus, timeSupplier, method, params, null);
        token.setResult(null);
        enqueue(message);
        return token;
    }

    private void enqueue(final JsonElement message) {
        outgoing.add(message);
        if (outgoing.length() == 1) {
            callback.run();
            executor.submit("send-rpc", this::send);
        }
    }

    private void send() {
        final JsonElement incoming;
        try {
            // write data
            final OutputInputStream ois = new OutputInputStream();
            final Writer writer = new OutputStreamWriter(ois, StandardCharsets.UTF_8);
            serializer.writeElement(writer, (outgoing.length() == 1 ? outgoing.get(0) : outgoing));
            // connect
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Length", String.valueOf(ois.size()));
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(5000);
            // send data
            try (final OutputStream out = connection.getOutputStream()) {
                IOUtils.copy(ois.getInputStream(), out);
            }
            // get response
            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                errorAllPending(new Error(String.valueOf(responseCode)));
                return;
            }
            // read data
            try (final Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                incoming = serializer.readElement(reader);
            }
        } catch (final IOException exc) {
            errorAllPending(new Error(exc));
            throw new RuntimeException(exc);
        }
        if (incoming.isArray()) {
            for (int index = 0; index < incoming.length(); index++) {
                handle(incoming.get(index));
            }
        } else {
            handle(incoming);
        }
        errorAllPending(new Error("wicked"));
    }

    private void errorAllPending(final Error error) {
        pending.values().forEach(token -> token.setError(error));
    }

    private void handle(final JsonElement message) {
        if (message.has("id")) {
            // id is present, assume this is a result or error response message.
            final JsonElement idElement = message.get("id");
            if (!idElement.isNumber()) {
                return;
            }
            final long id = idElement.getAsLong();
            final JsonRpcToken<?> token = pending.remove(id);
            if (token == null) {
                return;
            }
            try {
                if (message.has("error")) {
                    final JsonElement error = message.get("error");
                    token.setError(new Error(error.get("message").getAsString()));
                } else if (message.has("result")) {
                    final JsonElement result = message.get("result");
                    final Object object = serializer.elementToObject(result, token.getReturnType());
                    token.setResult(object);
                } else {
                    token.setError(new Error("no result, nor error."));
                }
            } catch (final RuntimeException exc) {
                exc.printStackTrace();
            }
        } else {
            throw new InternalError("notification not handled");
        }
    }
}
