package org.dru.dusap.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public abstract class AbstractJsonSerializer<R> implements JsonSerializer {
    public abstract R undefinedRaw();

    public abstract R nullRaw();

    public abstract Json jsonify(R raw);

    public abstract R nodify(Object any);

    public abstract <T> T decode(R raw, Class<T> type);

    public abstract void write(OutputStream out, R raw) throws IOException;

    public abstract void write(Writer out, R raw) throws IOException;

    public abstract String stringify(R raw);
}
