package org.dru.dusap.cache.lru;

import org.dru.dusap.inject.DependsOn;
import org.dru.dusap.inject.Expose;
import org.dru.dusap.inject.Module;
import org.dru.dusap.inject.Provides;
import org.dru.dusap.time.TimeModule;
import org.dru.dusap.time.TimeProvider;

import javax.inject.Singleton;

@DependsOn(TimeModule.class)
public final class LruCacheModule implements Module {
    @Provides
    @Expose
    @Singleton
    public LruCacheFactory getLruCacheFactory(final TimeProvider timeProvider) {
        return new LruCacheFactoryImpl(timeProvider);
    }
}
