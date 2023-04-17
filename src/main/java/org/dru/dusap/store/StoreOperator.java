package org.dru.dusap.store;

import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface StoreOperator<K extends Serializable, V extends Serializable> extends BiFunction<K, V, V> {
}
