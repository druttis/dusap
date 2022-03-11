package org.dru.dusap.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public interface Json {
    boolean isUndefined();

    boolean isNull();

    boolean isBoolean();

    boolean booleanValue();

    boolean isNumber();

    Number numberValue();

    boolean isIntegral();

    boolean isByte();

    byte byteValue();

    boolean isShort();

    short shortValue();

    boolean isInt();

    int intValue();

    boolean isLong();

    long longValue();

    boolean isFloat();

    float floatValue();

    boolean isDouble();

    double doubleValue();

    boolean isBigInteger();

    BigInteger bigIntegerValue();

    boolean isBigDecimal();

    BigDecimal bigDecimalValue();

    boolean isString();

    String stringValue();

    boolean isChar();

    char charValue();

    boolean isContainer();

    int size();

    void clear();

    boolean isArray();

    Json get(int index);

    void addUndefined();

    void addNull();

    void add(Object any);

    void insertUndefined(int index);

    void insertNull(int index);

    void insert(int index, Object any);

    Json setUndefined(int index);

    Json setNull(int index);

    Json set(int index, Object any);

    Json remove(int index);

    boolean isObject();

    Set<String> getIds();

    Json get(String id);

    Json putUndefined(String id);

    Json putNull(String id);

    Json put(String id, Object any);

    Json remove(String id);

    <T> T decode(Class<? extends T> type);

    void write(OutputStream out) throws IOException;

    void write(Writer out) throws IOException;

    String stringify();
}
