package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.Binding;
import org.dru.dusap.inject.Injector;
import org.dru.dusap.inject.Key;
import org.dru.dusap.util.LazyReference;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Objects;

public final class BindingImpl<T> implements Binding<T> {
    private final Key<T> key;
    private final boolean exposed;
    private final Provider<? extends T> unscoped;
    private final Annotation scope;
    private final LazyReference<Provider<? extends T>> providerRef;

    public BindingImpl(final Key<T> key, final boolean exposed, final Provider<? extends T> unscoped,
                       final Annotation scope, final Injector injector) {
        this.key = key;
        this.exposed = exposed;
        this.unscoped = unscoped;
        this.scope = scope;
        providerRef = LazyReference.by(() -> injector.getScopeHandler(scope).scope(scope, key, unscoped));
    }

    @Override
    public Key<T> getKey() {
        return key;
    }

    @Override
    public boolean isExposed() {
        return exposed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Provider<? extends T>> getProviderClass() {
        return (Class<? extends Provider<? extends T>>) unscoped.getClass();
    }

    @Override
    public Annotation getScope() {
        return scope;
    }

    @Override
    public T getInstance() {
        final Provider<? extends T> provider = providerRef.get();
        Objects.requireNonNull(provider, "ScopeHandler returned null provider");
        return provider.get();
    }

    @Override
    public String toString() {
        return "BindingImpl{" +
                "key=" + key +
                ", exposed=" + exposed +
                ", unscoped=" + unscoped +
                ", scope=" + scope +
                '}';
    }
}
