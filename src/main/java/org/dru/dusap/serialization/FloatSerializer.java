package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FloatSerializer extends AbstractTypeSerializer<Float> {
    private static final LazyReference<FloatSerializer> INSTANCE = LazyReference.by(FloatSerializer::new);

    public static FloatSerializer get() {
        return INSTANCE.get();
    }

    private FloatSerializer() {
        super(float.class, Float.class);
    }

    @Override
    public Float decode(final InputStream in) throws IOException {
        return InputStreamUtils.readFloat(in);
    }

    @Override
    public int byteLength(final Float val) throws IOException {
        return 4;
    }

    @Override
    public void encode(final OutputStream out, final Float val) throws IOException {
        OutputStreamUtils.writeFloat(out, val);
    }
}
