package org.dru.dusap.inject;

import java.lang.annotation.Annotation;

public interface ScopeBinding<S extends Annotation> {
    Class<S> annotationType();

    ScopeHandler<S> getScopeHandler();
}
