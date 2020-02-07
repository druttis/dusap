package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Scope;
import org.dru.dusap.injection.configurators.BindLink;
import org.dru.dusap.injection.configurators.BindName;
import org.dru.dusap.injection.configurators.ReferName;
import org.dru.dusap.injection.configurators.SpecifyScope;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

final class BindImpl<T> extends BCDE<T> implements BindName<T> {
    BindImpl(final InjectorImpl injector, final Class<T> type, final Object source) {
        super(injector, type, source);
    }

    @Override
    public void run() {
        getInjector().bind(getKey(), getSupplier(), getScope(), getSource());
    }

    @Override
    public BindLink<T> named(final String name) {
        specifyName(name);
        return this;
    }

    @Override
    public ReferName toBinding(final Class<? extends T> type) {
        return specifyBinding(type);
    }

    @Override
    public ReferName toBinding() {
        return specifyBinding(getKey().getType());
    }

    @Override
    public SpecifyScope toConstructor(final Constructor<? extends T> constructor) {
        specifyConstructor(constructor);
        return this;
    }

    @Override
    public SpecifyScope toInstance(final T instance) {
        specifyInstance(instance);
        return this;
    }

    @Override
    public SpecifyScope toSupplier(final Supplier<? extends T> supplier) {
        specifySupplier(supplier);
        return this;
    }

    @Override
    public SpecifyScope toType(final Class<? extends T> type) {
        specifyType(type);
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
