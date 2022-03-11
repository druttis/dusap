package org.dru.dusap.inject.node;

import org.dru.dusap.inject.Injector;
import org.dru.dusap.inject.KeyBuilder;
import org.dru.dusap.inject.Module;
import org.dru.dusap.inject.Query;
import org.dru.dusap.inject.binder.ReferenceBindingBuilder;
import org.dru.dusap.inject.provider.QueryProvider;
import org.dru.dusap.annotation.AnnotationBuilder;

import javax.inject.Provider;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.dru.dusap.annotation.Annotations.annotation;
import static org.dru.dusap.annotation.Annotations.requireAnnotatedWith;

final class BuilderSupplier<T> implements ReferenceBindingBuilder<T>, Supplier<Provider<T>> {
    private final KeyBuilder<T> keyBuilder;
    private final Injector injector;
    private final AtomicReference<Class<? extends Module>> fromRef;

    BuilderSupplier(final KeyBuilder<T> keyBuilder, final Injector injector) {
        Objects.requireNonNull(keyBuilder, "keyBuilder");
        Objects.requireNonNull(injector, "injector");
        this.keyBuilder = keyBuilder;
        this.injector = injector;
        fromRef = new AtomicReference<>(injector.getModule());
    }

    @Override
    public void from(final Class<? extends Module> source) {
        Objects.requireNonNull(source, "source");
        if (!fromRef.compareAndSet(injector.getModule(), source)) {
            throw new IllegalStateException("From already set");
        }
    }

    @Override
    public ReferenceBindingBuilder<T> with(final Annotation qualifier) {
        requireAnnotatedWith(qualifier, Qualifier.class);
        keyBuilder.with(qualifier);
        return this;
    }

    @Override
    public ReferenceBindingBuilder<T> with(final AnnotationBuilder<?> qualifierBuilder) {
        Objects.requireNonNull(qualifierBuilder, "qualifierBuilder");
        requireAnnotatedWith(qualifierBuilder.annotationType(), Qualifier.class);
        return with(qualifierBuilder.build());
    }

    @Override
    public ReferenceBindingBuilder<T> with(final Class<? extends Annotation> qualifierType) {
        requireAnnotatedWith(qualifierType, Qualifier.class);
        return with(annotation(qualifierType));
    }

    @Override
    public Provider<T> get() {
        return new QueryProvider<>(injector, Query.of(keyBuilder.build(), fromRef.get()));
    }
}
