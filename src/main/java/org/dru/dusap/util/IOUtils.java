package org.dru.dusap.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {
    public static long copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[8192];
        long totalBytes = 0;
        int readBytes;
        while ((readBytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, readBytes);
            totalBytes += readBytes;
        }
        return totalBytes;
    }

    public static byte[] readBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    private IOUtils() {
    }
}
