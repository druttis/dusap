package org.dru.dusap.conf;

import org.dru.dusap.inject.Expose;
import org.dru.dusap.inject.InjectorModule;
import org.dru.dusap.inject.Provides;

import javax.inject.Singleton;

public final class ConfModule implements InjectorModule {
    @Provides
    @Singleton
    @Expose
    public Conf getConf() {
        return new ConfImpl();
    }
}
