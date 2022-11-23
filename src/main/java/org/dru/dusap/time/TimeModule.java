package org.dru.dusap.time;

import org.dru.dusap.inject.Expose;
import org.dru.dusap.inject.InjectorModule;
import org.dru.dusap.inject.Provides;

import javax.inject.Singleton;

public final class TimeModule implements InjectorModule {
    @Provides
    @Expose
    @Singleton
    public TimeProvider getTimeProvider() {
        return new TimeProviderImpl();
    }
}
