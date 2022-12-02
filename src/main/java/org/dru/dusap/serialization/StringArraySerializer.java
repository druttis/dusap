package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

public enum StringArraySerializer implements TypeSerializer<String[]> {
    INSTANCE;

    @Override
    public String[] decode(final InputStream in) throws IOException {
        return InputStreamUtils.readStrings(in, InputStreamUtils.readVarInt(in));
    }

    @Override
    public int byteLength(final String[] val) throws IOException {
        final int sumUtfLength = Stream.of(val).mapToInt(DataUtils::utfLength).sum();
        return DataUtils.varLength(sumUtfLength, 32) + sumUtfLength;
    }

    @Override
    public void encode(final OutputStream out, final String[] val) throws IOException {
        OutputStreamUtils.writeVarInt(out, val.length);
        OutputStreamUtils.writeStrings(out, val);
    }
}
