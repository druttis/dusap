package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Binding;
import org.dru.dusap.injection.Key;
import org.dru.dusap.injection.Scope;

import java.util.Objects;
import java.util.function.Supplier;

final class BindingImpl<T> implements Binding<T> {
    private final Key<T> key;
    private final Object declarer;
    private Supplier<? extends T> supplier;
    private Scope scope;
    private Supplier<? extends T> scopedSupplier;
    private Object completer;
    private Object exposer;

    BindingImpl(final Key<T> key, final Object declarer) {
        this.key = Objects.requireNonNull(key, "key");
        this.declarer = Objects.requireNonNull(declarer, "declarer");
    }

    @Override
    public T getInstance() {
        return getScopedSupplier().get();
    }

    Key<T> getKey() {
        return key;
    }

    Object getDeclarer() {
        return declarer;
    }

    Supplier<? extends T> getSupplier() {
        if (supplier == null) {
            throw new IllegalStateException("supplier not specified");
        }
        return supplier;
    }

    void setSupplier(final Supplier<? extends T> supplier) {
        if (this.supplier != null) {
            throw new IllegalStateException("supplier already specified in " + completer);
        }
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    Scope getScope() {
        if (scope == null) {
            throw new IllegalStateException("scope not specified");
        }
        return scope;
    }

    void setScope(final Scope scope) {
        if (this.scope != null) {
            throw new IllegalStateException("scope already specified in " + declarer);
        }
        this.scope = Objects.requireNonNull(scope, "scope");
    }

    Supplier<? extends T> getScopedSupplier() {
        if (scopedSupplier == null) {
            scopedSupplier = getScope().scope(getKey(), getSupplier());
        }
        return scopedSupplier;
    }

    Object getCompleter() {
        return completer;
    }

    void setCompleter(final Object completer) {
        if (this.completer != null) {
            throw new IllegalStateException(completer + " - completer already specified in " + this.completer);
        }
        this.completer = Objects.requireNonNull(completer, "completer");
    }

    Object getExposer() {
        return exposer;
    }

    boolean isExposed() {
        return getExposer() != null;
    }

    void expose(final Object exposer) {
        if (isExposed()) {
            throw new IllegalStateException(exposer + " - already exposed in " + this.exposer);
        }
        this.exposer = Objects.requireNonNull(exposer, "exposer");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(key);
        if (declarer.equals(completer)) {
            sb.append(" bound in ").append(getDeclarer());
        } else {
            sb.append(" declared in ").append(getDeclarer());
            if (completer != null) {
                sb.append(", and completed in ").append(getCompleter());
            } else {
                sb.append(", incomplete");
            }
        }
        if (exposer != null) {
            sb.append(" - exposed in ").append(exposer);
        }
        return sb.toString();
    }
}
