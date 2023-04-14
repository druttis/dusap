package org.dru.dusap.serialization;

import org.dru.dusap.data.DataUtils;
import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StringSerializer extends AbstractTypeSerializer<String> {
    private static final LazyReference<StringSerializer> INSTANCE = LazyReference.by(StringSerializer::new);

    public static StringSerializer get() {
        return INSTANCE.get();
    }

    private StringSerializer() {
        super(String.class);
    }

    @Override
    public String decode(final InputStream in) throws IOException {
        return InputStreamUtils.readString(in);
    }

    @Override
    public int byteLength(final String val) throws IOException {
        final int utfLength = DataUtils.utfLength(val);
        return DataUtils.varLength(utfLength, 16) + utfLength;
    }

    @Override
    public void encode(final OutputStream out, final String val) throws IOException {
        OutputStreamUtils.writeString(out, val);
    }
}
