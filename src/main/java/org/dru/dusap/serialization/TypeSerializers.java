package org.dru.dusap.serialization;

public interface TypeSerializers {
    <T> TypeSerializer<T> get(Class<T> type);
}
