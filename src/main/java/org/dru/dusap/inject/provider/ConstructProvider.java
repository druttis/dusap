package org.dru.dusap.inject.provider;

import org.dru.dusap.inject.Injections;
import org.dru.dusap.inject.Injector;
import org.dru.dusap.reference.LazyReference;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.util.Objects;

public final class ConstructProvider<T> implements Provider<T> {
    private final TypeLiteral<T> typeLiteral;
    private final Injector injector;
    private final LazyReference<Constructor<T>> constructorRef;

    public ConstructProvider(final TypeLiteral<T> typeLiteral, final Injector injector) {
        Objects.requireNonNull(typeLiteral, "typeLiteral");
        Objects.requireNonNull(injector, "injector");
        this.typeLiteral = typeLiteral;
        this.injector = injector;
        constructorRef = LazyReference.by(() -> Injections.getInjectableConstructor(typeLiteral.getRawType()));
    }

    @Override
    public T get() {
        return injector.newInstance(constructorRef.get());
    }
}
