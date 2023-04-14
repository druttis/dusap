package org.dru.dusap.serialization;

import org.dru.dusap.json.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ObjectSerializer<T> extends AbstractTypeSerializer<T> {
    private static final Map<Class<?>, ObjectSerializer<?>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ObjectSerializer<T> get(final Class<T> type, final JsonSerializer jsonSerializer) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(jsonSerializer, "jsonSerializer");
        return (ObjectSerializer<T>) CACHE.computeIfAbsent(type, $ -> new ObjectSerializer<>(type, jsonSerializer));
    }

    private final Class<T> type;
    private final JsonSerializer jsonSerializer;

    private ObjectSerializer(final Class<T> type, final JsonSerializer jsonSerializer) {
        super(type);
        this.type = type;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public T decode(final InputStream in) throws IOException {
        return jsonSerializer.read(in).decode(type);
    }

    @Override
    public int byteLength(final T val) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsonSerializer.encode(val).write(out);
        return out.size();
    }

    @Override
    public void encode(final OutputStream out, final T val) throws IOException {
        jsonSerializer.encode(val).write(out);
    }
}
