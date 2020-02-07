package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.configurators.ExposeName;

final class ExposeImpl<T> extends BCDE<T> implements ExposeName {
    ExposeImpl(final InjectorImpl injector, final Class<T> type, final Object source) {
        super(injector, type, source);
    }

    @Override
    public void run() {
        getInjector().expose(getKey(), getSource());
    }

    @Override
    public void named(final String name) {
        specifyName(name);
    }
}
