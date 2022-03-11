package org.dru.dusap.inject;

public interface Module {
    default void configure(Binder binder) {
    }
}
