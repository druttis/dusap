package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteArraySerializer extends AbstractTypeSerializer<byte[]> {
    private static final LazyReference<ByteArraySerializer> INSTANCE = LazyReference.by(ByteArraySerializer::new);

    public static ByteArraySerializer get() {
        return INSTANCE.get();
    }

    private ByteArraySerializer() {
        super(byte[].class);
    }

    @Override
    public byte[] decode(final InputStream in) throws IOException {
        final int length = InputStreamUtils.readVarInt(in);
        return InputStreamUtils.readBytes(in, length);
    }

    @Override
    public int byteLength(final byte[] val) throws IOException {
        return val.length;
    }

    @Override
    public void encode(final OutputStream out, final byte[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeBytes(out, val);
    }
}
