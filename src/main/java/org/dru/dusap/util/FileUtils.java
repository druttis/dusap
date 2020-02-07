package org.dru.dusap.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class FileUtils {
    public static void delete(final Path path) throws IOException {
        for (final Path current : Files.walk(path).sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            Files.delete(current);
        }
    }

    private FileUtils() {
    }
}
