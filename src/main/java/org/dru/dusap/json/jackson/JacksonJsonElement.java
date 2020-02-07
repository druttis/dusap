package org.dru.dusap.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dru.dusap.json.JsonElement;

import java.util.HashMap;
import java.util.Map;

public final class JacksonJsonElement implements JsonElement {
    private static final JsonElement NULL = new JacksonJsonElement(NullNode.getInstance());

    public static JsonNode getNode(final JsonElement element) {
        return ((JacksonJsonElement) element).getNode();
    }

    private final JsonNode node;
    private final Map<JsonNode, JsonElement> cache;

    JacksonJsonElement(final JsonNode node) {
        this.node = node;
        cache = new HashMap<>();
    }

    @Override
    public boolean isArray() {
        return node.isArray();
    }

    @Override
    public boolean isBoolean() {
        return node.isBoolean();
    }

    @Override
    public boolean isNull() {
        return node.isNull();
    }

    @Override
    public boolean isNumber() {
        return node.isNumber();
    }

    @Override
    public boolean isObject() {
        return node.isObject();
    }

    @Override
    public boolean isString() {
        return node.isTextual();
    }

    @Override
    public int length() {
        return node.size();
    }

    @Override
    public void clear() {
        getContainerNode().removeAll();
    }

    @Override
    public JsonElement get(final int index) {
        return recall(getArrayNode().get(index));
    }

    @Override
    public void add(final int index, final JsonElement element) {
        getArrayNode().insert(index, remember(element));
    }

    @Override
    public void add(final JsonElement element) {
        getArrayNode().add(remember(element));
    }

    @Override
    public JsonElement set(final int index, final JsonElement element) {
        final JsonNode node = remember(element);
        final JsonNode oldNode = getArrayNode().set(index, node);
        if (oldNode == node) {
            return element;
        } else {
            return forget(oldNode);
        }
    }

    @Override
    public JsonElement remove(final int index) {
        return forget(getArrayNode().remove(index));
    }

    @Override
    public boolean getAsBoolean() {
        return getNode().booleanValue();
    }

    @Override
    public Byte getAsByte() {
        return (byte) getNode().shortValue();
    }

    @Override
    public Short getAsShort() {
        return getNode().shortValue();
    }

    @Override
    public Integer getAsInteger() {
        return getNode().intValue();
    }

    @Override
    public Long getAsLong() {
        return getNode().longValue();
    }

    @Override
    public Float getAsFloat() {
        return getNode().floatValue();
    }

    @Override
    public Double getAsDouble() {
        return getNode().doubleValue();
    }

    @Override
    public boolean has(final String name) {
        return getNode().has(name);
    }

    @Override
    public JsonElement get(final String name) {
        return recall(getObjectNode().get(name));
    }

    @Override
    public JsonElement put(final String name, final JsonElement element) {
        final JsonNode node = remember(element);
        final JsonNode oldNode = getObjectNode().set(name, node);
        if (node == oldNode) {
            return element;
        } else {
            return forget(node);
        }
    }

    @Override
    public JsonElement remove(final String name) {
        return forget(getObjectNode().remove(name));
    }

    @Override
    public String getAsString() {
        return getNode().textValue();
    }

    public JsonNode getNode() {
        return node;
    }

    public ContainerNode getContainerNode() {
        return (ContainerNode) getNode();
    }

    public ArrayNode getArrayNode() {
        return (ArrayNode) getNode();
    }

    public ObjectNode getObjectNode() {
        return (ObjectNode) getNode();
    }

    private JsonElement recall(final JsonNode node) {
        if (node == null) {
            return null;
        }
        return cache.compute(node, ($, element) -> {
            if (element == null) {
                element = new JacksonJsonElement(node);
            }
            return element;
        });
    }

    private JsonNode remember(final JsonElement element) {
        final JsonNode node = getNode(element);
        cache.putIfAbsent(node, element);
        return node;
    }

    private JsonElement forget(final JsonNode node) {
        return cache.remove(node);
    }
}
