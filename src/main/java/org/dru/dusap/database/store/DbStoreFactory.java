package org.dru.dusap.database.store;

import org.dru.dusap.store.Store;

public interface DbStoreFactory {
    <K, V> Store<K, V> newStore(final String configName, final String tableName, Class<K> keyType,
                                Class<V> valueType);
}
