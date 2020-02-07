package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Module;
import org.dru.dusap.injection.configurators.ReferModule;
import org.dru.dusap.injection.configurators.ReferName;

import java.util.function.Supplier;

final class ReferImpl<T> extends BCDE<T> implements ReferName, Supplier<T> {
    private InjectorImpl source;

    ReferImpl(final InjectorImpl injector, final Class<T> type, final Object source) {
        super(injector, type, source);
    }

    @Override
    public void run() {
        throw new RuntimeException("nono");
    }

    @Override
    public ReferModule named(final String name) {
        specifyName(name);
        return this;
    }

    @Override
    public void in(final Class<? extends Module> module) {
        if (source != null) {
            throw new IllegalStateException("source already specified");
        }
        source = getInjector().getDependency(module);
    }

    @Override
    public T get() {
        return source.getInstance(getKey());
    }
}
