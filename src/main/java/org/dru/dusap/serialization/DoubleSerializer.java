package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DoubleSerializer extends AbstractTypeSerializer<Double> {
    private static final LazyReference<DoubleSerializer> INSTANCE = LazyReference.by(DoubleSerializer::new);

    public static DoubleSerializer get() {
        return INSTANCE.get();
    }

    private DoubleSerializer() {
        super(double.class, Double.class);
    }

    @Override
    public Double decode(final InputStream in) throws IOException {
        return InputStreamUtils.readDouble(in);
    }

    @Override
    public int byteLength(final Double val) throws IOException {
        return 8;
    }

    @Override
    public void encode(final OutputStream out, final Double val) throws IOException {
        OutputStreamUtils.writeDouble(out, val);
    }
}
