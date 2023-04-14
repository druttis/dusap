package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class CharArraySerializer extends AbstractTypeSerializer<char[]> {
    private static final LazyReference<CharArraySerializer> INSTANCE = LazyReference.by(CharArraySerializer::new);

    public static CharArraySerializer get() {
        return INSTANCE.get();
    }

    private CharArraySerializer() {
        super(char[].class);
    }

    @Override
    public char[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readChars(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final char[] val) throws IOException {
        return val.length * 2;
    }

    @Override
    public void encode(final OutputStream out, final char[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeChars(out, val);
    }
}
