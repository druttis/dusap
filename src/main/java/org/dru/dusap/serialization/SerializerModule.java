package org.dru.dusap.serialization;

import org.dru.dusap.inject.Expose;
import org.dru.dusap.inject.InjectorModule;
import org.dru.dusap.inject.Provides;
import org.dru.dusap.json.JsonSerializer;

import javax.inject.Singleton;

public final class SerializerModule implements InjectorModule {
    @Provides
    @Singleton
    @Expose
    public TypeSerializers getTypeSerializers(final JsonSerializer jsonSerializer) {
        return new TypeSerializersImpl(jsonSerializer);
    }
}
