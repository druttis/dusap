package org.dru.dusap.inject;

public interface InjectorModule {
    default void configure(Binder binder) {
    }
}
