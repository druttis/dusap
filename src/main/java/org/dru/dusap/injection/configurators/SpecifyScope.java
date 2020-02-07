package org.dru.dusap.injection.configurators;

import org.dru.dusap.injection.Scope;

public interface SpecifyScope {
    void asSingleton();

    void toScope(Scope scope);
}
