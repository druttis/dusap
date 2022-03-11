package org.dru.dusap.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface JsonSerializer {
    Json getUndefined();

    Json getNull();

    Json newArray();

    Json newObject();

    Json encode(Object any);

    Json read(InputStream in) throws IOException;

    Json read(Reader in) throws IOException;

    Json parse(String json);
}
