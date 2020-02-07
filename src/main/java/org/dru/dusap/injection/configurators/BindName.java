package org.dru.dusap.injection.configurators;

public interface BindName<T> extends BindLink<T> {
    BindLink<T> named(String name);
}
