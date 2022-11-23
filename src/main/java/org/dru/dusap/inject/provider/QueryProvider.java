package org.dru.dusap.inject.provider;

import org.dru.dusap.inject.Injector;
import org.dru.dusap.inject.InjectorQuery;

import javax.inject.Provider;

public class QueryProvider<T> implements Provider<T> {
    private final Injector injector;
    private final InjectorQuery<T> query;

    public QueryProvider(final Injector injector, final InjectorQuery<T> query) {
        this.injector = injector;
        this.query = query;
    }

    @Override
    public T get() {
        return injector.getInstance(query);
    }
}
