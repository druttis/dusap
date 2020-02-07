package org.dru.dusap.injection.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Utils {
    public static List<StackTraceElement> getStackTrace() {
        final Throwable exc = new Throwable();
        exc.fillInStackTrace();
        final List<StackTraceElement> list = new ArrayList<>(Arrays.asList(exc.getStackTrace()));
        list.remove(0);
        return list;
    }

    public static StackTraceElement getStackTrace(final int callerLevel) {
        return getStackTrace().get(callerLevel + 1);
    }

    public static <E extends Throwable> void raise(final E exc, final int callerLevel) throws E {
        final List<StackTraceElement> list = getStackTrace();
        exc.setStackTrace(list.subList(callerLevel + 1, list.size()).toArray(new StackTraceElement[0]));
        throw exc;
    }

    private Utils() {
    }
}
