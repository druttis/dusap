package org.dru.dusap.time;

import org.dru.dusap.inject.Expose;
import org.dru.dusap.inject.Module;
import org.dru.dusap.inject.Provides;

import javax.inject.Singleton;

public final class TimeModule implements Module {
    @Provides
    @Expose
    @Singleton
    public TimeProvider getTimeProvider() {
        return new TimeProviderImpl();
    }
}
