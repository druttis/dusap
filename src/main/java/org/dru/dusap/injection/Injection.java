package org.dru.dusap.injection;

import org.dru.dusap.injection.internal.InjectorContext;

public final class Injection {
    public static Injector getInjector(final Class<? extends Module> module) {
        return (new InjectorContext()).getInjector(module);
    }

    private Injection() {
    }
}
