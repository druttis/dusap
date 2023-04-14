package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class BooleanArraySerializer extends AbstractTypeSerializer<boolean[]> {
    private static final LazyReference<BooleanArraySerializer> INSTANCE = LazyReference.by(BooleanArraySerializer::new);

    public static BooleanArraySerializer get() {
        return INSTANCE.get();
    }

    private BooleanArraySerializer() {
        super(boolean[].class);
    }

    @Override
    public boolean[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readBits(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final boolean[] val) throws IOException {
        return (val.length + 7) >> 3;
    }

    @Override
    public void encode(final OutputStream out, final boolean[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeBits(out, val);
    }
}
