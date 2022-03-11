package org.dru.dusap.inject;

import org.dru.dusap.inject.binder.ExposedBindingBuilder;
import org.dru.dusap.inject.binder.LinkedScopeBindingBuilder;
import org.dru.dusap.inject.binder.QualifiedBindingBuilder;
import org.dru.dusap.util.TypeLiteral;

import java.lang.annotation.Annotation;

public interface Binder {
    <A extends Annotation, T extends ScopeHandler<A>> LinkedScopeBindingBuilder<A, T> bindScope(Class<A> scopeType);

    <T> ExposedBindingBuilder<T> bind(KeyBuilder<T> keyBuilder);

    <T> ExposedBindingBuilder<T> bind(Key<T> key);

    <T> QualifiedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    <T> QualifiedBindingBuilder<T> bind(Class<T> type);
}
