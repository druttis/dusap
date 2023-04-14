package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ShortSerializer extends AbstractTypeSerializer<Short> {
    private static final LazyReference<ShortSerializer> INSTANCE = LazyReference.by(ShortSerializer::new);

    public static ShortSerializer get() {
        return INSTANCE.get();
    }

    private ShortSerializer() {
        super(short.class, Short.class);
    }

    @Override
    public Short decode(final InputStream in) throws IOException {
        return InputStreamUtils.readShort(in);
    }

    @Override
    public int byteLength(final Short val) throws IOException {
        return 2;
    }

    @Override
    public void encode(final OutputStream out, final Short val) throws IOException {
        OutputStreamUtils.writeShort(out, val);
    }
}
