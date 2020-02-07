package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Key;
import org.dru.dusap.injection.Scope;
import org.dru.dusap.reference.LazyReference;

import java.util.Objects;
import java.util.function.Supplier;

public enum Scopes implements Scope {
    NO_SCOPE {
        @Override
        public <T> Supplier<? extends T> scope(final Key<T> key, final Supplier<? extends T> supplier) {
            return Objects.requireNonNull(supplier, "supplier");
        }
    },
    SINGLETON {
        @Override
        public <T> Supplier<? extends T> scope(final Key<T> key, final Supplier<? extends T> supplier) {
            return LazyReference.by(supplier)::get;
        }
    };
}
