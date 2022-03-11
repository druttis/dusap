package org.dru.dusap.inject.binder;

import org.dru.dusap.inject.Key;
import org.dru.dusap.inject.KeyBuilder;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Provider;

public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder {
    ScopedBindingBuilder toProvider(Provider<? extends T> provider);

    void toValue(T value);

    ScopedBindingBuilder toType(TypeLiteral<? extends T> typeLiteral);

    ScopedBindingBuilder toType(Class<? extends T> type);

    <U extends T> ForeignBindingBuilder toBinding(KeyBuilder<U> keyBuilder);

    <U extends T> ForeignBindingBuilder toBinding(Key<U> key);

    <U extends T> ReferenceBindingBuilder<U> toBinding(TypeLiteral<U> typeLiteral);

    <U extends T> ReferenceBindingBuilder<U> toBiding(Class<U> type);
}
