package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IntArraySerializer extends AbstractTypeSerializer<int[]> {
    private static final LazyReference<IntArraySerializer> INSTANCE = LazyReference.by(IntArraySerializer::new);

    public static IntArraySerializer get() {
        return INSTANCE.get();
    }

    private IntArraySerializer() {
        super(int[].class);
    }

    @Override
    public int[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readVarInts(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final int[] val) throws IOException {
        int len = DataUtils.varLength(val.length, 32);
        for (final int x : val) {
            len += DataUtils.varLength(x, 32);
        }
        return len;
    }

    @Override
    public void encode(final OutputStream out, final int[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeVarInts(out, val);
    }
}
