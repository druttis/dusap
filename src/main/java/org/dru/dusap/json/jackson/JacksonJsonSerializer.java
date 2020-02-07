package org.dru.dusap.json.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import org.dru.dusap.json.JsonElement;
import org.dru.dusap.json.JsonSerializer;

import java.io.*;

public final class JacksonJsonSerializer implements JsonSerializer {
    private final JacksonJsonElement NULL = new JacksonJsonElement(NullNode.getInstance());

    private final ObjectMapper mapper;
    private final JsonNodeFactory factory;

    JacksonJsonSerializer() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
        factory = new JsonNodeFactory(true);
    }

    @Override
    public JsonElement readElement(final InputStream in) throws IOException {
        return newJsonElement(mapper.readTree(in));
    }

    @Override
    public JsonElement readElement(final Reader reader) throws IOException {
        return newJsonElement(mapper.readTree(reader));
    }

    @Override
    public JsonElement stringToElement(final String string) {
        try {
            return newJsonElement(mapper.readTree(string));
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public JsonElement objectToElement(final Object object) {
        return newJsonElement(mapper.valueToTree(object));
    }

    @Override
    public void writeElement(final OutputStream out, final JsonElement element) throws IOException {
        mapper.writeValue(out, getNode(element));
    }

    @Override
    public void writeElement(final Writer writer, final JsonElement element) throws IOException {
        mapper.writeValue(writer, getNode(element));
    }

    @Override
    public String elementToString(final JsonElement element) {
        try {
            return mapper.writeValueAsString(element);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public <T> T readObject(final InputStream in, final Class<T> type) throws IOException {
        return mapper.readValue(in, type);
    }

    @Override
    public <T> T readObject(final Reader reader, final Class<T> type) throws IOException {
        return mapper.readValue(reader, type);
    }

    @Override
    public <T> T stringToObject(final String string, final Class<T> type) {
        try {
            return mapper.readValue(string, type);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public <T> T elementToObject(final JsonElement element, final Class<T> type) {
        try {
            return mapper.treeToValue(getNode(element), type);
        } catch (final JsonProcessingException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void writeObject(final OutputStream out, final Object object) throws IOException {
        mapper.writeValue(out, object);
    }

    @Override
    public void writeObject(final Writer writer, final Object object) throws IOException {
        mapper.writeValue(writer, object);
    }

    @Override
    public String objectToString(final Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (final JsonProcessingException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public JsonElement newArray() {
        return newJsonElement(factory.arrayNode());
    }

    @Override
    public JsonElement newBoolean(final boolean value) {
        return newJsonElement(factory.booleanNode(value));
    }

    @Override
    public JsonElement newNull() {
        return NULL;
    }

    @Override
    public JsonElement newNumber(final byte value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newNumber(final short value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newNumber(final int value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newNumber(final long value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newNumber(final float value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newNumber(final double value) {
        return newJsonElement(factory.numberNode(value));
    }

    @Override
    public JsonElement newObject() {
        return newJsonElement(factory.objectNode());
    }

    @Override
    public JsonElement newString(final String value) {
        return newJsonElement(factory.textNode(value));
    }

    private JsonNode getNode(final JsonElement element) {
        return JacksonJsonElement.getNode(element);
    }

    private JsonElement newJsonElement(final JsonNode node) {
        return new JacksonJsonElement(node);
    }
}
