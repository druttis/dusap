package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Module;
import org.dru.dusap.injection.configurators.CompleteLink;
import org.dru.dusap.injection.configurators.CompleteModule;
import org.dru.dusap.injection.configurators.CompleteName;
import org.dru.dusap.injection.configurators.ReferName;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

final class CompleteImpl<T> extends BCDE<T> implements CompleteName<T>, CompleteLink<T> {
    private InjectorImpl target;

    CompleteImpl(final InjectorImpl injector, final Class<T> type, final Object source) {
        super(injector, type, source);
    }

    @Override
    public void run() {
        getTarget().complete(getKey(), getSupplier(), getSource());
    }

    @Override
    public CompleteModule<T> named(final String name) {
        return null;
    }

    @Override
    public CompleteLink<T> in(final Class<? extends Module> module) {
        if (target != null) {
            throw new IllegalStateException("target already specified");
        }
        target = getInjector().getDependency(module);
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
    public void toConstructor(final Constructor<? extends T> constructor) {
        specifyConstructor(constructor);
    }

    @Override
    public void toInstance(final T instance) {
        specifyInstance(instance);
    }

    @Override
    public void toSupplier(final Supplier<? extends T> supplier) {
        specifySupplier(supplier);
    }

    @Override
    public void toType(final Class<? extends T> type) {
        specifyType(type);
    }

    private InjectorImpl getTarget() {
        if (target == null) {
            throw new IllegalStateException("target not specified");
        }
        return target;
    }
}
