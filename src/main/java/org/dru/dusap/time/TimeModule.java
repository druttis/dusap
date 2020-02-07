package org.dru.dusap.time;


import org.dru.dusap.injection.Module;

public final class TimeModule extends Module {
    public TimeModule() {
    }

    @Override
    protected void configure() {
        bind(TimeSupplier.class).toType(TimeSupplierImpl.class).asSingleton();
        expose(TimeSupplier.class);
    }
}
