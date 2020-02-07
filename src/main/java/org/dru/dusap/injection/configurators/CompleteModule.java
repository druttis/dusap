package org.dru.dusap.injection.configurators;

import org.dru.dusap.injection.Module;

public interface CompleteModule<T> {
    CompleteLink<T> in(Class<? extends Module> module);
}
