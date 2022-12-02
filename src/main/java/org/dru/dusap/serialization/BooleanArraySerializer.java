package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum BooleanArraySerializer implements TypeSerializer<boolean[]> {
    INSTANCE;

    @Override
    public boolean[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readBits(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final boolean[] val) throws IOException {
        return (val.length + 7) >> 3;
    }

    @Override
    public void encode(final OutputStream out, final boolean[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeBits(out, val);
    }
}
