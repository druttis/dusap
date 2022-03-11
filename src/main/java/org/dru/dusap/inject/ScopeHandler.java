package org.dru.dusap.inject;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

public interface ScopeHandler<S extends Annotation> {
    <T> Provider<? extends T> scope(S scope, Key<T> key, Provider<? extends T> unscoped);
}
