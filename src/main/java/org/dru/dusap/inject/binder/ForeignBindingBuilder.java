package org.dru.dusap.inject.binder;

import org.dru.dusap.inject.InjectorModule;

public interface ForeignBindingBuilder {
    void from(Class<? extends InjectorModule> source);
}
