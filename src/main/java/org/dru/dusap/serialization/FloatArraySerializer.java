package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FloatArraySerializer extends AbstractTypeSerializer<float[]> {
    private static final LazyReference<FloatArraySerializer> INSTANCE = LazyReference.by(FloatArraySerializer::new);

    public static FloatArraySerializer get() {
        return INSTANCE.get();
    }

    private FloatArraySerializer() {
        super(float[].class);
    }

    @Override
    public float[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readFloats(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final float[] val) throws IOException {
        return val.length * 4;
    }

    @Override
    public void encode(final OutputStream out, final float[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeFloats(out, val);
    }
}
