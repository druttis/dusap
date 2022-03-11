package org.dru.dusap.inject.binder;

import org.dru.dusap.inject.Module;

public interface ForeignBindingBuilder {
    void from(Class<? extends Module> source);
}
