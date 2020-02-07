package org.dru.dusap.time;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class TimeUtils {
    public static TimeUnit convert(final ChronoUnit cu) {
        if (cu == null) {
            return null;
        }
        switch (cu) {
            case DAYS:
                return TimeUnit.DAYS;
            case HOURS:
                return TimeUnit.HOURS;
            case MINUTES:
                return TimeUnit.MINUTES;
            case SECONDS:
                return TimeUnit.SECONDS;
            case MICROS:
                return TimeUnit.MICROSECONDS;
            case MILLIS:
                return TimeUnit.MILLISECONDS;
            case NANOS:
                return TimeUnit.NANOSECONDS;
            default:
                throw new UnsupportedOperationException("can not convert " + cu + " to a TimeUnit");
        }
    }

    public static ChronoUnit convert(final TimeUnit tu) {
        if (tu == null) {
            return null;
        }
        switch (tu) {
            case DAYS:
                return ChronoUnit.DAYS;
            case HOURS:
                return ChronoUnit.HOURS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            default:
                throw new UnsupportedOperationException("can not convert " + tu + " to a ChronoUnit");
        }
    }

    private TimeUtils() {
    }
}
