package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.ScopeBinding;
import org.dru.dusap.inject.ScopeHandler;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Objects;

public final class ScopeBindingImpl<S extends Annotation> implements ScopeBinding<S> {
    private final Class<S> scopeType;
    private final Provider<? extends ScopeHandler<S>> provider;

    public ScopeBindingImpl(final Class<S> scopeType, final Provider<? extends ScopeHandler<S>> provider) {
        Objects.requireNonNull(scopeType, "scopeType");
        Objects.requireNonNull(provider, "provider");
        this.scopeType = scopeType;
        this.provider = provider;
    }

    @Override
    public Class<S> annotationType() {
        return scopeType;
    }

    @Override
    public ScopeHandler<S> getScopeHandler() {
        return provider.get();
    }
}
