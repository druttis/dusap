package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Key;
import org.dru.dusap.injection.Scope;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.Supplier;

abstract class BCDE<T> implements Runnable {
    private final InjectorImpl injector;
    private Key<T> key;
    private final Object source;
    private Supplier<? extends T> supplier;
    private Scope scope;

    BCDE(final InjectorImpl injector, final Class<T> type, final Object source) {
        this.injector = Objects.requireNonNull(injector, "injector");
        key = Key.of(type);
        this.source = (source != null ? source : Utils.getStackTrace(0));
    }

    final InjectorImpl getInjector() {
        return injector;
    }

    final Key<T> getKey() {
        return key;
    }

    final Object getSource() {
        return source;
    }

    final Supplier<? extends T> getSupplier() {
        if (supplier == null) {
            throw new IllegalStateException("supplier not specified");
        }
        return supplier;
    }

    final Scope getScope() {
        if (scope == null) {
            throw new IllegalStateException("scope not specified");
        }
        return scope;
    }

    final void specifyName(final String name) {
        key = key.named(name);
    }

    final ReferImpl<? extends T> specifyBinding(final Class<? extends T> type) {
        final ReferImpl<? extends T> refer = new ReferImpl<>(getInjector(), type, source);
        specifySupplier(refer);
        specifyScope(Scopes.NO_SCOPE);
        return refer;
    }

    final void specifyConstructor(final Constructor<? extends T> constructor) {
        Objects.requireNonNull(constructor, "constructor");
        specifySupplier(() -> getInjector().newInstance(constructor, true));
    }

    final void specifyInstance(final T instance) {
        specifySupplier(() -> instance);
        specifyScope(Scopes.NO_SCOPE);
    }

    final void specifySupplier(final Supplier<? extends T> supplier) {
        if (this.supplier != null) {
            throw new IllegalStateException("supplier already specified");
        }
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    final void specifyType(final Class<? extends T> type) {
        Objects.requireNonNull(type, "type");
        specifySupplier(() -> getInjector().newInstance(type, true));
    }

    final void specifyScope(final Scope scope) {
        if (this.scope != null) {
            throw new IllegalStateException("scope already specified");
        }
        this.scope = Objects.requireNonNull(scope, "scope");
    }
}
