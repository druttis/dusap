package org.dru.dusap.inject.binder;

public interface ExposedBindingBuilder<T> extends LinkedBindingBuilder<T> {
    LinkedBindingBuilder<T> exposed();
}
