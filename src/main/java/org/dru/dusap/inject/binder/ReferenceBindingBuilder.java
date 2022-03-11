package org.dru.dusap.inject.binder;

import org.dru.dusap.util.AnnotationBuilder;

import java.lang.annotation.Annotation;

public interface ReferenceBindingBuilder<T> extends ForeignBindingBuilder {
    ReferenceBindingBuilder<T> with(Annotation qualifier);

    ReferenceBindingBuilder<T> with(AnnotationBuilder<?> qualifierBuilder);

    ReferenceBindingBuilder<T> with(Class<? extends Annotation> qualifierType);
}
