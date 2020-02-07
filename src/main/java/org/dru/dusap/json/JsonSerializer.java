package org.dru.dusap.json;

import java.io.*;

public interface JsonSerializer {
    JsonElement readElement(InputStream in) throws IOException;

    JsonElement readElement(Reader reader) throws IOException;

    JsonElement stringToElement(String string);

    JsonElement objectToElement(Object object);

    void writeElement(OutputStream out, JsonElement element) throws IOException;

    void writeElement(Writer writer, JsonElement element) throws IOException;

    String elementToString(JsonElement element);

    <T> T readObject(InputStream in, Class<T> type) throws IOException;

    <T> T readObject(Reader reader, Class<T> type) throws IOException;

    <T> T stringToObject(String string, Class<T> type);

    <T> T elementToObject(JsonElement element, Class<T> type);

    void writeObject(OutputStream out, Object object) throws IOException;

    void writeObject(Writer writer, Object object) throws IOException;

    String objectToString(Object object);

    JsonElement newArray();

    JsonElement newBoolean(boolean value);

    JsonElement newNull();

    JsonElement newNumber(byte value);

    JsonElement newNumber(short value);

    JsonElement newNumber(int value);

    JsonElement newNumber(long value);

    JsonElement newNumber(float value);

    JsonElement newNumber(double value);

    JsonElement newObject();

    JsonElement newString(String value);
}
