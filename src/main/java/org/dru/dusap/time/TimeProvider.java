package org.dru.dusap.time;

import java.time.Instant;

public interface TimeProvider {
    Instant getCurrentTime();
}
