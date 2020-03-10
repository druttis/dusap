package org.dru.dusap.database.store;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface DbStore<K, V> {
    Map<K, V> getAll(Collection<K> keys, boolean locked) throws SQLException;

    V get(K key, boolean locked) throws SQLException;

    void putAll(Map<K, V> entries) throws SQLException;

    void put(K key, V value) throws SQLException;

    Map<K, V> computeAll(Collection<K> keys, UnaryOperator<V> operator) throws SQLException;

    V compute(K key, UnaryOperator<V> operator) throws SQLException;

    Set<K> removeAll(Collection<K> keys) throws SQLException;

    boolean remove(K key) throws SQLException;
}
