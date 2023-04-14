package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IntSerializer extends AbstractTypeSerializer<Integer> {
    private static final LazyReference<IntSerializer> INSTANCE = LazyReference.by(IntSerializer::new);

    public static IntSerializer get() {
        return INSTANCE.get();
    }

    private IntSerializer() {
        super(int.class, Integer.class);
    }

    @Override
    public Integer decode(final InputStream in) throws IOException {
        return InputStreamUtils.readVarInt(in);
    }

    @Override
    public int byteLength(final Integer val) throws IOException {
        return DataUtils.varLength(val, 32);
    }

    @Override
    public void encode(final OutputStream out, final Integer val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val);
    }
}
