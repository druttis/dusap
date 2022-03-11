package org.dru.dusap.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractJson<R> implements Json {
    protected transient final AbstractJsonSerializer<R> serializer;
    private transient final Map<R, AbstractJson<R>> cache;

    protected AbstractJson(final AbstractJsonSerializer<R> serializer) {
        Objects.requireNonNull(serializer, "serializer");
        this.serializer = serializer;
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public final boolean isByte() {
        return isLongInRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    public final byte byteValue() {
        return (byte) longValueOrError(Byte.MIN_VALUE, Byte.MAX_VALUE, "byte");
    }

    @Override
    public final boolean isShort() {
        return isLongInRange(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public final short shortValue() {
        return (short) longValueOrError(Short.MIN_VALUE, Short.MAX_VALUE, "short");
    }

    @Override
    public final boolean isInt() {
        return isLongInRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public final int intValue() {
        return (int) longValueOrError(Integer.MIN_VALUE, Integer.MAX_VALUE, "int");
    }

    @Override
    public final boolean isFloat() {
        return isDoubleInRange(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    @Override
    public final float floatValue() {
        return doubleValueOrError(Float.MIN_VALUE, Float.MAX_VALUE, "float");
    }

    @Override
    public final boolean isDouble() {
        return false;
    }

    @Override
    public final boolean isChar() {
        return isString() && stringValue().length() == 1;
    }

    @Override
    public final char charValue() {
        if (!isString() || stringValue().length() != 1) {
            throw new IllegalArgumentException("Non-string value, expected string with length of 1");
        }
        return stringValue().charAt(0);
    }

    @Override
    public final int size() {
        requireContainer();
        return sizeImpl();
    }

    @Override
    public final void clear() {
        if (!isContainer()) {
            throw new IllegalArgumentException("Not a json container");
        }
        clearImpl();
    }

    @Override
    public final AbstractJson<R> get(final int index) {
        requireArray();
        return remember(getRaw(index));
    }

    @Override
    public final void addUndefined() {
        requireArray();
        addRaw(serializer.undefinedRaw());
    }

    @Override
    public final void addNull() {
        requireArray();
        addRaw(serializer.nullRaw());
    }

    @Override
    public final void add(final Object any) {
        requireArray();
        addRaw(serializer.nodify(any));
    }

    @Override
    public final void insertUndefined(final int index) {
        requireArray();
        insertRaw(index, serializer.undefinedRaw());
    }

    @Override
    public final void insertNull(final int index) {
        requireArray();
        insertRaw(index, serializer.nullRaw());
    }

    @Override
    public final void insert(final int index, final Object any) {
        requireArray();
        insertRaw(index, serializer.nodify(any));
    }

    @Override
    public final AbstractJson<R> setUndefined(final int index) {
        return setImpl(index, serializer.undefinedRaw());
    }

    @Override
    public final AbstractJson<R> setNull(final int index) {
        return setImpl(index, serializer.nullRaw());
    }

    @Override
    public final AbstractJson<R> set(final int index, final Object any) {
        return setImpl(index, serializer.nodify(any));
    }

    @Override
    public final AbstractJson<R> remove(final int index) {
        requireArray();
        return forget(removeRaw(index));
    }

    @Override
    public final Set<String> getIds() {
        requireObject();
        return getIdsImpl();
    }

    @Override
    public final AbstractJson<R> get(final String id) {
        requireObject();
        return remember(getRaw(id));
    }

    @Override
    public final AbstractJson<R> putUndefined(final String id) {
        return putImpl(id, serializer.undefinedRaw());
    }

    @Override
    public final AbstractJson<R> putNull(final String id) {
        return putImpl(id, serializer.nullRaw());
    }

    @Override
    public final AbstractJson<R> put(final String id, final Object any) {
        return putImpl(id, serializer.nodify(any));
    }

    @Override
    public final AbstractJson<R> remove(final String id) {
        requireObject();
        return forget(removeRaw(id));
    }

    @Override
    public final <T> T decode(final Class<? extends T> type) {
        return serializer.decode(raw(), type);
    }

    @Override
    public final void write(final OutputStream out) throws IOException {
        serializer.write(out, raw());
    }

    @Override
    public final void write(final Writer out) throws IOException {
        serializer.write(out, raw());
    }

    @Override
    public final String stringify() {
        return serializer.stringify(raw());
    }

    protected final void requireContainer() {
        if (!isContainer()) {
            throw new JsonException("Not a json container");
        }
    }

    protected final void requireArray() {
        if (!isArray()) {
            throw new JsonException("Not an json array");
        }
    }

    protected final void requireObject() {
        if (!isObject()) {
            throw new JsonException("Not a json object");
        }
    }

    protected final boolean isLongInRange(final long min, final long max) {
        if (!isIntegral()) {
            return false;
        }
        final long value = longValue();
        return (value >= min && value <= max);
    }

    protected final void requireIntegral(final String expectedType) {
        if (!isIntegral()) {
            throw new IllegalArgumentException("Non-integral, expected " + expectedType);
        }
    }

    protected final long longValueOrError(final long min, final long max, final String expectedType) {
        requireIntegral(expectedType);
        final long value = longValue();
        if (value < min || value > max) {
            throw new IllegalArgumentException(expectedType + " value out of range: " + value);
        }
        return value;
    }

    @SuppressWarnings("SameParameterValue")
    protected final boolean isDoubleInRange(final double min, final double max) {
        final double value = doubleValue();
        return (value >= min && value <= max);
    }

    protected final void requireNumber(final String expectedType) {
        if (!isNumber()) {
            throw new IllegalArgumentException("Non-number, expected " + expectedType);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected final long doubleValueOrError(final double min, final double max, final String expectedType) {
        requireNumber(expectedType);
        final long value = longValue();
        if (value < min || value > max) {
            throw new IllegalArgumentException(expectedType + " value out of range: " + value);
        }
        return value;
    }

    private AbstractJson<R> setImpl(final int index, final R raw) {
        requireArray();
        return forget(setRaw(index, raw));
    }

    private AbstractJson<R> putImpl(final String id, final R raw) {
        requireObject();
        return forget(putRaw(id, raw));
    }

    @SuppressWarnings("unchecked")
    private AbstractJson<R> remember(final R raw) {
        if (raw == serializer.undefinedRaw()) {
            return (AbstractJson<R>) serializer.getUndefined();
        } else if (raw == serializer.nullRaw()) {
            return (AbstractJson<R>) serializer.getNull();
        } else {
            return cache.computeIfAbsent(raw, $ -> (AbstractJson<R>) serializer.jsonify(raw));
        }
    }

    @SuppressWarnings("unchecked")
    private AbstractJson<R> forget(final R key) {
        final AbstractJson<R> json = cache.remove(key);
        return (json != null ? json : (AbstractJson<R>) serializer.getUndefined());
    }

    protected abstract R raw();

    protected abstract int sizeImpl();

    protected abstract void clearImpl();

    protected abstract R getRaw(int index);

    protected abstract void addRaw(R raw);

    protected abstract void insertRaw(int index, R raw);

    protected abstract R setRaw(int index, R raw);

    protected abstract R removeRaw(int index);

    protected abstract Set<String> getIdsImpl();

    protected abstract R getRaw(String id);

    protected abstract R putRaw(final String id, final R raw);

    protected abstract R removeRaw(String id);
}
