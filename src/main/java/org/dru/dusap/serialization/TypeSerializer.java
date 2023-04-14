package org.dru.dusap.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TypeSerializer<T> {
    Class<?>[] getTypes();

    T decode(InputStream in) throws IOException;

    int byteLength(T val) throws IOException;

    void encode(OutputStream out, T val) throws IOException;
}
