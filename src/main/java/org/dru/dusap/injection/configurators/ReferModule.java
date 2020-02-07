package org.dru.dusap.injection.configurators;

import org.dru.dusap.injection.Module;

public interface ReferModule {
    void in(Class<? extends Module> module);
}
