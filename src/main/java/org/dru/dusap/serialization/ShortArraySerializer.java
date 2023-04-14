package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ShortArraySerializer extends AbstractTypeSerializer<short[]> {
    private static final LazyReference<ShortArraySerializer> INSTANCE = LazyReference.by(ShortArraySerializer::new);

    public static ShortArraySerializer get() {
        return INSTANCE.get();
    }

    private ShortArraySerializer() {
        super(short[].class);
    }

    @Override
    public short[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readShorts(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final short[] val) throws IOException {
        return 0;
    }

    @Override
    public void encode(final OutputStream out, final short[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeShorts(out, val);
    }
}
