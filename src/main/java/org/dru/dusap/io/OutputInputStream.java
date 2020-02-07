package org.dru.dusap.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class OutputInputStream extends ByteArrayOutputStream {
    public OutputInputStream() {
    }

    public OutputInputStream(final int size) {
        super(size);
    }

    public final synchronized InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(buf, 0, size());
        } finally {
            buf = null;
        }
    }
}
