package org.dru.dusap.inject.node;

import org.dru.dusap.inject.*;
import org.dru.dusap.inject.binder.LinkedBindingBuilder;
import org.dru.dusap.inject.binder.QualifiedBindingBuilder;
import org.dru.dusap.inject.binder.ReferenceBindingBuilder;
import org.dru.dusap.inject.binder.ScopedBindingBuilder;
import org.dru.dusap.inject.provider.ConstructProvider;
import org.dru.dusap.inject.provider.ValueProvider;
import org.dru.dusap.annotation.AnnotationBuilder;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.dru.dusap.annotation.Annotations.annotation;
import static org.dru.dusap.annotation.Annotations.requireAnnotatedWith;
import static org.dru.dusap.inject.Keys.key;

public final class BindingNode<T> implements Node, QualifiedBindingBuilder<T> {
    private final KeyBuilder<T> keyBuilder;
    private final Injector injector;
    private final AtomicBoolean exposedRef;
    private final AtomicReference<Supplier<? extends Provider<? extends T>>> providerSupplierRef;
    private final AtomicReference<Annotation> scopeRef;

    public BindingNode(final KeyBuilder<T> keyBuilder, final Injector injector) {
        Objects.requireNonNull(keyBuilder, "keyBuilder");
        Objects.requireNonNull(injector, "injector");
        this.keyBuilder = keyBuilder;
        this.injector = injector;
        exposedRef = new AtomicBoolean();
        providerSupplierRef = new AtomicReference<>();
        scopeRef = new AtomicReference<>();
    }

    @Override
    public <R, D> R accept(final NodeVisitor<R, D> visitor, final D input) {
        return visitor.visitBindingNode(this, input);
    }

    @Override
    public QualifiedBindingBuilder<T> with(final Annotation qualifier) {
        keyBuilder.with(qualifier);
        return this;
    }

    @Override
    public QualifiedBindingBuilder<T> with(final AnnotationBuilder<?> qualifierBuilder) {
        Objects.requireNonNull(qualifierBuilder, "qualifierBuilder");
        requireAnnotatedWith(qualifierBuilder.annotationType(), Qualifier.class);
        return with(qualifierBuilder.build());
    }

    @Override
    public QualifiedBindingBuilder<T> with(final Class<? extends Annotation> qualifierType) {
        return with(annotation(qualifierType));
    }

    @Override
    public LinkedBindingBuilder<T> exposed() {
        if (!exposedRef.compareAndSet(false, true)) {
            throw new IllegalArgumentException("Already exposed");
        }
        return this;
    }

    @Override
    public ScopedBindingBuilder toProvider(final Provider<? extends T> provider) {
        Objects.requireNonNull(provider, "provider");
        setProviderSupplier(() -> provider);
        return this;
    }

    @Override
    public void toValue(final T value) {
        toProvider(new ValueProvider<>(value));
    }

    @Override
    public ScopedBindingBuilder toType(final TypeLiteral<? extends T> typeLiteral) {
        return toProvider(new ConstructProvider<>(typeLiteral, injector));
    }

    @Override
    public ScopedBindingBuilder toType(final Class<? extends T> type) {
        return toType(TypeLiteral.of(type));
    }

    @Override
    public <U extends T> BuilderSupplier<U> toBinding(final KeyBuilder<U> keyBuilder) {
        final BuilderSupplier<U> builderSupplier = new BuilderSupplier<>(keyBuilder, injector);
        setProviderSupplier(builderSupplier);
        return builderSupplier;
    }

    @Override
    public <U extends T> BuilderSupplier<U> toBinding(final Key<U> key) {
        Objects.requireNonNull(key, "key");
        return toBinding(key.builder());
    }

    @Override
    public <U extends T> ReferenceBindingBuilder<U> toBinding(final TypeLiteral<U> typeLiteral) {
        return toBinding(key(typeLiteral));
    }

    @Override
    public <U extends T> ReferenceBindingBuilder<U> toBiding(final Class<U> type) {
        return toBinding(TypeLiteral.of(type));
    }

    @Override
    public void in(final Annotation scope) {
        requireAnnotatedWith(scope, Scope.class);
        if (!scopeRef.compareAndSet(null, scope)) {
            throw new IllegalStateException("Scope already set");
        }
    }

    @Override
    public void in(final AnnotationBuilder<?> scopeBuilder) {
        Objects.requireNonNull(scopeBuilder, "scopeBuilder");
        requireAnnotatedWith(scopeBuilder.annotationType(), Scope.class);
        in(scopeBuilder.annotationType());
    }

    @Override
    public void in(final Class<? extends Annotation> scopeType) {
        requireAnnotatedWith(scopeType, Scope.class);
        in(annotation(scopeType));
    }

    public Key<T> getKey() {
        return keyBuilder.build();
    }

    public boolean isExposed() {
        return exposedRef.get();
    }

    public Provider<? extends T> getProvider() {
        return providerSupplierRef.get().get();
    }

    public Annotation getScope() {
        return scopeRef.get();
    }

    private void setProviderSupplier(final Supplier<? extends Provider<? extends T>> providerSupplier) {
        if (!providerSupplierRef.compareAndSet(null, providerSupplier)) {
            throw new IllegalStateException("Provider supplier already set");
        }
    }
}
