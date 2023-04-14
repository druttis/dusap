package org.dru.dusap.serialization;

import org.dru.dusap.io.InputStreamUtils;
import org.dru.dusap.io.OutputStreamUtils;
import org.dru.dusap.reference.LazyReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class CharSerializer extends AbstractTypeSerializer<Character> {
    private static final LazyReference<CharSerializer> INSTANCE = LazyReference.by(CharSerializer::new);

    public static CharSerializer get() {
        return INSTANCE.get();
    }

    private CharSerializer() {
        super(char.class, Character.class);
    }

    @Override
    public Character decode(final InputStream in) throws IOException {
        return InputStreamUtils.readChar(in);
    }

    @Override
    public int byteLength(final Character val) throws IOException {
        return 2;
    }

    @Override
    public void encode(final OutputStream out, final Character val) throws IOException {
        OutputStreamUtils.writeChar(out, val);
    }
}
