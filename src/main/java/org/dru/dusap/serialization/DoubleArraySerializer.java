package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DoubleArraySerializer extends AbstractTypeSerializer<double[]> {
    private static final LazyReference<DoubleArraySerializer> INSTANCE = LazyReference.by(DoubleArraySerializer::new);

    public static DoubleArraySerializer get() {
        return INSTANCE.get();
    }

    private DoubleArraySerializer() {
        super(double[].class);
    }

    @Override
    public double[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readDoubles(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final double[] val) throws IOException {
        return val.length * 8;
    }

    @Override
    public void encode(final OutputStream out, final double[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeDoubles(out, val);
    }
}
