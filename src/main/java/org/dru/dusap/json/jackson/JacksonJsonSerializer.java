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
import org.dru.dusap.json.AbstractJsonSerializer;
import org.dru.dusap.json.JsonException;

import java.io.*;

public class JacksonJsonSerializer extends AbstractJsonSerializer<JsonNode> {
    private final ObjectMapper mapper;
    private final JsonNodeFactory factory;
    private final JacksonJson undefinedJson;
    private final JacksonJson nullJson;

    public JacksonJsonSerializer() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
        factory = new JsonNodeFactory(true);
        undefinedJson = new JacksonJson(this, undefinedRaw());
        nullJson = new JacksonJson(this, nullRaw());
    }

    @Override
    public JacksonJson getUndefined() {
        return undefinedJson;
    }

    @Override
    public JacksonJson getNull() {
        return nullJson;
    }

    @Override
    public JacksonJson newArray() {
        return jsonify(mapper.createArrayNode());
    }

    @Override
    public JacksonJson newObject() {
        return jsonify(mapper.createObjectNode());
    }

    @Override
    public JacksonJson encode(final Object any) {
        return jsonify(nodify(any));
    }

    @Override
    public JacksonJson read(final InputStream in) throws IOException {
        return jsonify(mapper.readTree(in));
    }

    @Override
    public JacksonJson read(final Reader in) throws IOException {
        return jsonify(mapper.readTree(in));
    }

    @Override
    public JacksonJson parse(final String json) {
        try {
            return jsonify(mapper.readTree(json));
        } catch (final JsonProcessingException exc) {
            throw new JsonException(exc);
        }
    }

    @Override
    public JsonNode undefinedRaw() {
        return mapper.missingNode();
    }

    @Override
    public JsonNode nullRaw() {
        return mapper.nullNode();
    }

    @Override
    public JacksonJson jsonify(final JsonNode raw) {
        if (raw == undefinedRaw()) {
            return getUndefined();
        } else if (raw == nullRaw()) {
            return getNull();
        } else {
            return new JacksonJson(this, raw);
        }
    }

    @Override
    public JsonNode nodify(final Object any) {
        if (any == null || any == nullRaw()) {
            return nullRaw();
        } else if (any == undefinedRaw()) {
            return undefinedRaw();
        } else if (any instanceof JsonNode) {
            return (JsonNode) any;
        } else if (any instanceof JacksonJson) {
            return ((JacksonJson) any).raw();
        } else {
            return mapper.valueToTree(any);
        }
    }

    @Override
    public <T> T decode(final JsonNode raw, final Class<T> type) {
        try {
            return mapper.treeToValue(raw, type);
        } catch (final JsonProcessingException exc) {
            throw new JsonException(exc);
        }
    }

    @Override
    public void write(final OutputStream out, final JsonNode raw) throws IOException {
        mapper.writeValue(out, raw);
    }

    @Override
    public void write(final Writer out, final JsonNode raw) throws IOException {
        mapper.writeValue(out, raw);
    }

    @Override
    public String stringify(final JsonNode raw) {
        try {
            return mapper.writeValueAsString(raw);
        } catch (final JsonProcessingException exc) {
            throw new JsonException(exc);
        }
    }
}
