package org.dru.dusap.inject.binder;

import org.dru.dusap.annotation.AnnotationBuilder;

import java.lang.annotation.Annotation;

public interface QualifiedBindingBuilder<T> extends ExposedBindingBuilder<T> {
    QualifiedBindingBuilder<T> with(Annotation qualifier);

    QualifiedBindingBuilder<T> with(AnnotationBuilder<?> qualifierBuilder);

    QualifiedBindingBuilder<T> with(Class<? extends Annotation> qualifierType);
}
