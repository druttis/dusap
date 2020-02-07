package org.dru.dusap.injection.configurators;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

public interface BindLink<T> extends SpecifyScope {
    ReferName toBinding(Class<? extends T> type);

    ReferName toBinding();

    SpecifyScope toConstructor(Constructor<? extends T> constructor);

    SpecifyScope toInstance(T instance);

    SpecifyScope toSupplier(Supplier<? extends T> supplier);

    SpecifyScope toType(Class<? extends T> type);
}
