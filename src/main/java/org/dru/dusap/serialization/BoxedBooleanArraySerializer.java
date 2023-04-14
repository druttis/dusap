package org.dru.dusap.serialization;

import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.IntStream;

public final class BoxedBooleanArraySerializer extends AbstractTypeSerializer<Boolean[]> {
    private static final LazyReference<BoxedBooleanArraySerializer> INSTANCE
            = LazyReference.by(BoxedBooleanArraySerializer::new);

    public static BoxedBooleanArraySerializer get() {
        return INSTANCE.get();
    }

    private static boolean[] primitive(final Boolean[] in) {
        final boolean[] out = new boolean[in.length];
        for (int index = 0; index < in.length; index++) {
            out[index] = in[index];
        }
        return out;
    }

    private BoxedBooleanArraySerializer() {
        super(Boolean[].class);
    }

    @Override
    public Boolean[] decode(final InputStream in) throws IOException {
        final boolean[] res = BooleanArraySerializer.get().decode(in);
        return IntStream.range(0, res.length).mapToObj(i -> res[i]).toArray(Boolean[]::new);
    }

    @Override
    public int byteLength(final Boolean[] val) throws IOException {
        return BooleanArraySerializer.get().byteLength(primitive(val));
    }

    @Override
    public void encode(final OutputStream out, final Boolean[] val) throws IOException {
        BooleanArraySerializer.get().encode(out, primitive(val));
    }
}
