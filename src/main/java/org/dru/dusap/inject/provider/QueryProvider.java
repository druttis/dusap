package org.dru.dusap.inject.provider;

import org.dru.dusap.inject.Injector;
import org.dru.dusap.inject.Query;

import javax.inject.Provider;

public class QueryProvider<T> implements Provider<T> {
    private final Injector injector;
    private final Query<T> query;

    public QueryProvider(final Injector injector, final Query<T> query) {
        this.injector = injector;
        this.query = query;
    }

    @Override
    public T get() {
        return injector.getInstance(query);
    }
}
