package org.dru.dusap.injection;

import java.util.function.Supplier;

public interface Scope {
    <T> Supplier<? extends T> scope(Key<T> key, Supplier<? extends T> supplier);
}
