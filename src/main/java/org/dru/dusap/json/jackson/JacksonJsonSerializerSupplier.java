package org.dru.dusap.json.jackson;

import org.dru.dusap.json.JsonSerializer;
import org.dru.dusap.json.JsonSerializerSupplier;

public final class JacksonJsonSerializerSupplier implements JsonSerializerSupplier {
    private final JacksonJsonSerializer instance;

    public JacksonJsonSerializerSupplier() {
        instance = new JacksonJsonSerializer();
    }

    @Override
    public JsonSerializer get() {
        return instance;
    }
}
