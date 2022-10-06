package org.dru.dusap.time;

import java.time.Instant;

public final class TimeProviderImpl implements TimeProvider {
    public TimeProviderImpl() {
    }

    @Override
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
