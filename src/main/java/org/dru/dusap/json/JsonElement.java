package org.dru.dusap.json;

public interface JsonElement {
    boolean isArray();

    boolean isBoolean();

    boolean isNull();

    boolean isNumber();

    boolean isObject();

    boolean isString();

    int length();

    void clear();

    JsonElement get(int index);

    void add(int index, JsonElement element);

    void add(JsonElement element);

    JsonElement set(int index, JsonElement element);

    JsonElement remove(int index);

    boolean getAsBoolean();

    Byte getAsByte();

    Short getAsShort();

    Integer getAsInteger();

    Long getAsLong();

    Float getAsFloat();

    Double getAsDouble();

    boolean has(String name);

    JsonElement get(String name);

    JsonElement put(String name, JsonElement element);

    JsonElement remove(String name);

    String getAsString();
}
