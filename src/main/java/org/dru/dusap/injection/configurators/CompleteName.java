package org.dru.dusap.injection.configurators;

public interface CompleteName<T> extends CompleteModule<T> {
    CompleteModule<T> named(String name);
}
