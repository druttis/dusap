package org.dru.dusap.injection.configurators;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

public interface CompleteLink<T> {
    ReferName toBinding(Class<? extends T> type);

    ReferName toBinding();

    void toConstructor(Constructor<? extends T> constructor);

    void toInstance(T instance);

    void toSupplier(Supplier<? extends T> supplier);

    void toType(Class<? extends T> type);
}
