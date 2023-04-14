package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

public final class StringArraySerializer extends AbstractTypeSerializer<String[]> {
    private static final LazyReference<StringArraySerializer> INSTANCE = LazyReference.by(StringArraySerializer::new);

    public static StringArraySerializer get() {
        return INSTANCE.get();
    }

    private StringArraySerializer() {
        super(String[].class);
    }

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
