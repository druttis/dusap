package org.dru.dusap.inject.node;

import org.dru.dusap.inject.*;
import org.dru.dusap.inject.binder.LinkedScopeBindingBuilder;
import org.dru.dusap.inject.binder.ReferenceBindingBuilder;
import org.dru.dusap.inject.provider.ValueProvider;
import org.dru.dusap.util.Annotations;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Provider;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class ScopeBindingNode<A extends Annotation, T extends ScopeHandler<A>>
        implements Node, LinkedScopeBindingBuilder<A, T> {
    private final Class<A> scopeType;
    private final Injector injector;
    private final AtomicReference<Supplier<Provider<T>>> providerSupplierRef;

    public ScopeBindingNode(final Class<A> scopeType, final Injector injector) {
        Objects.requireNonNull(scopeType, "scopeType");
        Annotations.requireAnnotatedWith(scopeType, Scope.class);
        Objects.requireNonNull(injector, "injector");
        this.scopeType = scopeType;
        this.injector = injector;
        providerSupplierRef = new AtomicReference<>();
    }

    @Override
    public <R, D> R accept(final NodeVisitor<R, D> visitor, final D input) {
        return visitor.visitScopeBindingNode(this, input);
    }

    @Override
    public void toProvider(final Provider<T> provider) {
        setProviderSupplier(() -> provider);
    }

    @Override
    public void toValue(final T scopeHandler) {
        toProvider(new ValueProvider<>(scopeHandler));
    }

    @Override
    public BuilderSupplier<T> toBinding(final KeyBuilder<T> keyBuilder) {
        final BuilderSupplier<T> builderSupplier = new BuilderSupplier<>(keyBuilder, injector);
        setProviderSupplier(builderSupplier);
        return builderSupplier;
    }

    @Override
    public BuilderSupplier<T> toBinding(final Key<T> key) {
        return toBinding(KeyBuilder.of(key));
    }

    @Override
    public ReferenceBindingBuilder<T> toBinding(final TypeLiteral<T> typeLiteral) {
        return toBinding(KeyBuilder.of(typeLiteral));
    }

    @Override
    public ReferenceBindingBuilder<T> toBiding(final Class<T> type) {
        return toBinding(TypeLiteral.of(type));
    }

    public Class<A> getScopeType() {
        return scopeType;
    }

    public Provider<T> getProvider() {
        return providerSupplierRef.get().get();
    }

    private void setProviderSupplier(final Supplier<Provider<T>> providerSupplier) {
        if (!providerSupplierRef.compareAndSet(null, providerSupplier)) {
            throw new IllegalStateException("Provider supplier already set");
        }
    }
}
