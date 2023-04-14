package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class BooleanSerializer extends AbstractTypeSerializer<Boolean> {
    private static final LazyReference<BooleanSerializer> INSTANCE = LazyReference.by(BooleanSerializer::new);

    public static BooleanSerializer get() {
        return INSTANCE.get();
    }

    private BooleanSerializer() {
        super(boolean.class, Boolean.class);
    }

    @Override
    public Boolean decode(final InputStream in) throws IOException {
        return InputStreamUtils.readBoolean(in);
    }

    @Override
    public int byteLength(final Boolean val) throws IOException {
        return 1;
    }

    @Override
    public void encode(final OutputStream out, final Boolean val) throws IOException {
        OutputStreamUtils.writeBoolean(out, val);
    }
}
