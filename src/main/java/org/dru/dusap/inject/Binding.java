package org.dru.dusap.inject;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

public interface Binding<T> {
    Key<T> getKey();

    boolean isExposed();

    Class<? extends Provider<? extends T>> getProviderClass();

    Annotation getScope();

    T getInstance();
}
