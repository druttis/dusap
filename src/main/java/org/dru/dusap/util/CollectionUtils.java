package org.dru.dusap.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CollectionUtils {
    public static <T> List<T> asList(final T first, T... rest) {
        final List<T> result = new ArrayList<>();
        result.add(first);
        result.addAll(Arrays.asList(rest));
        return result;
    }

    private CollectionUtils() {
    }
}
