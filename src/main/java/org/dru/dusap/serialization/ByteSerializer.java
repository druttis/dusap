package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteSerializer extends AbstractTypeSerializer<Byte> {
    private static final LazyReference<ByteSerializer> INSTANCE = LazyReference.by(ByteSerializer::new);

    public static ByteSerializer get() {
        return INSTANCE.get();
    }

    private ByteSerializer() {
        super(byte.class, Byte.class);
    }

    @Override
    public Byte decode(final InputStream in) throws IOException {
        return InputStreamUtils.readByte(in);
    }

    @Override
    public int byteLength(final Byte val) throws IOException {
        return 1;
    }

    @Override
    public void encode(final OutputStream out, final Byte val) throws IOException {
        OutputStreamUtils.writeByte(out, val);
    }
}
