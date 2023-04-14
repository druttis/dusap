package org.dru.dusap.serialization;

import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.json.jackson.JacksonJsonSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class TypeSerializersImpl implements TypeSerializers {
    private final JsonSerializer jsonSerializer;
    private final Map<Class<?>, TypeSerializer<?>> typeSerializerByType;

    public TypeSerializersImpl(final JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        typeSerializerByType = new HashMap<>();
        registerTypeSerializer(BooleanArraySerializer.get());
        registerTypeSerializer(BooleanSerializer.get());
        registerTypeSerializer(ByteArraySerializer.get());
        registerTypeSerializer(ByteSerializer.get());
        registerTypeSerializer(CharArraySerializer.get());
        registerTypeSerializer(CharSerializer.get());
        registerTypeSerializer(DoubleArraySerializer.get());
        registerTypeSerializer(DoubleSerializer.get());
        registerTypeSerializer(FloatArraySerializer.get());
        registerTypeSerializer(FloatSerializer.get());
        registerTypeSerializer(IntArraySerializer.get());
        registerTypeSerializer(IntSerializer.get());
        registerTypeSerializer(LongArraySerializer.get());
        registerTypeSerializer(LongSerializer.get());
        registerTypeSerializer(ShortArraySerializer.get());
        registerTypeSerializer(ShortSerializer.get());
        registerTypeSerializer(StringArraySerializer.get());
        registerTypeSerializer(StringSerializer.get());
        registerTypeSerializer(BoxedBooleanArraySerializer.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeSerializer<T> get(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        TypeSerializer<?> typeSerializer = typeSerializerByType.get(type);
        if (typeSerializer == null) {
            typeSerializer = ObjectSerializer.get(type, jsonSerializer);
        }
        return (TypeSerializer<T>) typeSerializer;
    }

    private void registerTypeSerializer(final TypeSerializer<?> typeSerializer) {
        Stream.of(typeSerializer.getTypes()).forEach(type -> typeSerializerByType.put(type, typeSerializer));
    }

    public static void main(String[] args) throws IOException {
        final boolean[] orig = {true, false, true};
        TypeSerializers tsm = new TypeSerializersImpl(new JacksonJsonSerializer());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        tsm.get(boolean[].class).encode(out, orig);
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        System.out.println(Arrays.asList(tsm.get(Boolean[].class).decode(in)));
    }
}
