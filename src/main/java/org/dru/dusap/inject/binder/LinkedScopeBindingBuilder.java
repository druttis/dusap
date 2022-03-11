package org.dru.dusap.inject.binder;

import org.dru.dusap.inject.Key;
import org.dru.dusap.inject.KeyBuilder;
import org.dru.dusap.inject.ScopeHandler;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

public interface LinkedScopeBindingBuilder<A extends Annotation, T extends ScopeHandler<A>> {
    void toProvider(Provider<T> provider);

    void toValue(T scopeHandler);

    ForeignBindingBuilder toBinding(KeyBuilder<T> keyBuilder);

    ForeignBindingBuilder toBinding(Key<T> key);

    ReferenceBindingBuilder<T> toBinding(TypeLiteral<T> typeLiteral);

    ReferenceBindingBuilder<T> toBiding(Class<T> type);
}
