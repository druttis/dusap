package org.dru.dusap.inject;

import org.dru.dusap.inject.internal.InjectorContextImpl;

public final class Injection {
    public static Injector createInjector(Class<? extends InjectorModule> module) {
        return (new InjectorContextImpl()).createInjector(module);
    }

    private Injection() {
    }
}
