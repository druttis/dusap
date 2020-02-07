package org.dru.dusap.injection;

import org.dru.dusap.injection.internal.Context;

public final class Injection {
    public static Injector getInjector(final Class<? extends Module> module) {
        return (new Context()).getInjector(module);
    }

    private Injection() {
    }
}
