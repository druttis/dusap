package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.Key;
import org.dru.dusap.inject.ScopeHandler;
import org.dru.dusap.util.LazyReference;
import org.dru.dusap.util.Reference;

import javax.inject.Provider;
import javax.inject.Singleton;

public enum SingletonScopeHandler implements ScopeHandler<Singleton> {
    INSTANCE;

    @Override
    public <T> Provider<? extends T> scope(final Singleton singleton, final Key<T> Key,
                                           final Provider<? extends T> unscoped) {
        return new SingletonProvider<>(unscoped);
    }

    private static final class SingletonProvider<T> implements Provider<T> {
        private final Reference<T> reference;

        public SingletonProvider(final Provider<T> unscoped) {
            reference = LazyReference.by(unscoped::get);
        }

        @Override
        public T get() {
            return reference.get();
        }
    }
}
