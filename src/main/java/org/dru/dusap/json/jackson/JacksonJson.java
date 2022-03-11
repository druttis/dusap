package org.dru.dusap.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dru.dusap.json.AbstractJson;
import org.dru.dusap.json.AbstractJsonSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public final class JacksonJson extends AbstractJson<JsonNode> {
    private final JsonNode raw;

    JacksonJson(final AbstractJsonSerializer<JsonNode> serializer, final JsonNode raw) {
        super(serializer);
        this.raw = raw;
    }

    @Override
    public boolean isUndefined() {
        return raw.isMissingNode();
    }

    @Override
    public boolean isNull() {
        return raw.isNull();
    }

    @Override
    public boolean isBoolean() {
        return raw.isBoolean();
    }

    @Override
    public boolean booleanValue() {
        return raw.booleanValue();
    }

    @Override
    public boolean isNumber() {
        return raw.isNumber();
    }

    @Override
    public Number numberValue() {
        return raw.numberValue();
    }

    @Override
    public boolean isIntegral() {
        return raw.isIntegralNumber();
    }

    @Override
    public boolean isLong() {
        return isIntegral() && raw.canConvertToLong();
    }

    @Override
    public long longValue() {
        requireIntegral("long");
        return raw.longValue();
    }

    @Override
    public double doubleValue() {
        requireNumber("double");
        return raw.doubleValue();
    }

    @Override
    public boolean isBigInteger() {
        return raw.isBigInteger();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return raw.bigIntegerValue();
    }

    @Override
    public boolean isBigDecimal() {
        return raw.isBigDecimal();
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return raw.decimalValue();
    }

    @Override
    public boolean isString() {
        return raw.isTextual();
    }

    @Override
    public String stringValue() {
        return raw.textValue();
    }

    @Override
    public boolean isContainer() {
        return raw.isContainerNode();
    }

    @Override
    public boolean isArray() {
        return raw.isArray();
    }

    @Override
    public boolean isObject() {
        return raw.isObject();
    }

    @Override
    public JsonNode raw() {
        return raw;
    }

    @Override
    protected int sizeImpl() {
        return raw.size();
    }

    @Override
    protected void clearImpl() {
        ((ContainerNode<?>) raw).removeAll();
    }

    @Override
    protected JsonNode getRaw(final int index) {
        return raw.get(index);
    }

    @Override
    protected void addRaw(final JsonNode raw) {
        ((ArrayNode) raw).add(raw);
    }

    @Override
    protected void insertRaw(final int index, final JsonNode raw) {
        ((ArrayNode) raw).insert(index, raw);
    }

    @Override
    protected JsonNode setRaw(final int index, final JsonNode raw) {
        return ((ArrayNode) raw).set(index, raw);
    }

    @Override
    protected JsonNode removeRaw(final int index) {
        return ((ArrayNode) raw).remove(index);
    }

    @Override
    protected Set<String> getIdsImpl() {
        final Set<String> ids = new HashSet<>();
        raw.fieldNames().forEachRemaining(ids::add);
        return ids;
    }

    @Override
    protected JsonNode getRaw(final String id) {
        return raw.get(id);
    }

    @Override
    protected JsonNode putRaw(final String id, final JsonNode raw) {
        return ((ObjectNode) raw).set(id, raw);
    }

    @Override
    protected JsonNode removeRaw(final String id) {
        return ((ObjectNode) raw).remove(id);
    }
}
