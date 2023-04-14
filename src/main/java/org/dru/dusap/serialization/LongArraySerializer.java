package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class LongArraySerializer extends AbstractTypeSerializer<long[]> {
    private static final LazyReference<LongArraySerializer> INSTANCE = LazyReference.by(LongArraySerializer::new);

    public static LongArraySerializer get() {
        return INSTANCE.get();
    }

    private LongArraySerializer() {
        super(long[].class);
    }

    @Override
    public long[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readVarLongs(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final long[] val) throws IOException {
        int len = DataUtils.varLength(val.length, 32);
        for (final long x : val) {
            len += DataUtils.varLength(x, 64);
        }
        return len;
    }

    @Override
    public void encode(final OutputStream out, final long[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeVarLongs(out, val);
    }
}
