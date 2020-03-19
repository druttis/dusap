package org.dru.dusap.util;

import java.util.*;

public final class CollectionUtils {
    @SuppressWarnings("unchecked")
    public static <T> List<T> asList(final T first, T... rest) {
        final List<T> result = new ArrayList<>();
        result.add(first);
        result.addAll(Arrays.asList(rest));
        return result;
    }

    public static <C extends Collection<T>, T> C requireNonNull(final C coll, final String name, String item) {
        Objects.requireNonNull(coll, name);
        coll.forEach(v -> Objects.requireNonNull(v, item + " in " + name));
        return coll;
    }

    private CollectionUtils() {
    }
}
