package org.dru.dusap.json;

import org.dru.dusap.conf.Conf;
import org.dru.dusap.conf.ConfModule;
import org.dru.dusap.inject.*;
import org.dru.dusap.inject.InjectorModule;

import javax.inject.Singleton;
import javax.naming.ConfigurationException;

@DependsOn(ConfModule.class)
public final class JsonModule implements InjectorModule {
    private static final String SERIALIZE_CLASSNAME_KEY = "json.serializer.className";
    private static final String DEFAULT_SERIALIZER_CLASSNAME = "org.dru.dusap.json.jackson.JacksonJsonSerializer";

    @Provides
    @Singleton
    @Expose
    public JsonSerializer getJsonSerializer(final Conf conf, final Injector injector)
            throws ClassNotFoundException, ConfigurationException {
        final String serializerClassName = conf.getOrDefault(SERIALIZE_CLASSNAME_KEY, DEFAULT_SERIALIZER_CLASSNAME);
        final Class<?> serializerClass;
        serializerClass = Class.forName(serializerClassName);
        if (!JsonSerializer.class.isAssignableFrom(serializerClass)) {
            throw new ConfigurationException("Not a json serializer class: " + serializerClassName);
        }
        return (JsonSerializer) injector.newInstance(serializerClass);
    }
}
