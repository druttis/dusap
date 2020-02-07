package org.dru.dusap.functional;

public final class Throwing {
    @SuppressWarnings("unchecked")
    public static <E extends Throwable, R> R raise(final Throwable t) throws E {
        throw (E) t;
    }

    private Throwing() {
    }
}
