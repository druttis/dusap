package org.dru.dusap.inject;

public interface InjectorContext {
    Injector getInjector(Class<? extends InjectorModule> module);
}
