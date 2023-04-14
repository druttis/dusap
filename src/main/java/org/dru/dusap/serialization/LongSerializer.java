package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class LongSerializer extends AbstractTypeSerializer<Long> {
    private static final LazyReference<LongSerializer> INSTANCE = LazyReference.by(LongSerializer::new);

    public static LongSerializer get() {
        return INSTANCE.get();
    }

    private LongSerializer() {
        super(long.class, Long.class);
    }

    @Override
    public Long decode(final InputStream in) throws IOException {
        return InputStreamUtils.readVarLong(in);
    }

    @Override
    public int byteLength(final Long val) throws IOException {
        return DataUtils.varLength(val, 64);
    }

    @Override
    public void encode(final OutputStream out, final Long val) throws IOException {
        OutputStreamUtils.writeVarLong(out, val);
    }
}
