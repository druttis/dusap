package org.dru.dusap.inject.binder;

import org.dru.dusap.annotation.AnnotationBuilder;

import java.lang.annotation.Annotation;

public interface ScopedBindingBuilder {
    void in(Annotation scope);

    void in(AnnotationBuilder<?> scopeBuilder);

    void in(Class<? extends Annotation> scopeType);
}
