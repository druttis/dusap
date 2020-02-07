package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Scope;
import org.dru.dusap.injection.configurators.DeclareName;
import org.dru.dusap.injection.configurators.SpecifyScope;

final class DeclareImpl<T> extends BCDE<T> implements DeclareName {
    DeclareImpl(final InjectorImpl injector, final Class<T> type, final Object source) {
        super(injector, type, source);
    }

    @Override
    public void run() {
        getInjector().declare(getKey(), getScope(), getSource());
    }

    @Override
    public SpecifyScope named(final String name) {
        specifyName(name);
        return this;
    }

    @Override
    public void asSingleton() {
        specifyScope(Scopes.SINGLETON);
    }

    @Override
    public void toScope(final Scope scope) {
        specifyScope(scope);
    }
}
