package org.dru.dusap.json;

import org.dru.dusap.injection.Module;
import org.dru.dusap.json.jackson.JacksonJsonSerializerSupplier;

public final class JsonModule extends Module {
    public JsonModule() {
    }

    @Override
    protected void configure() {
        bind(JsonSerializerSupplier.class).toType(JacksonJsonSerializerSupplier.class).asSingleton();
        expose(JsonSerializerSupplier.class);
    }
}
