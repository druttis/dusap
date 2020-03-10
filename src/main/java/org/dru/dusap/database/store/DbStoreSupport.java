package org.dru.dusap.database.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface DbStoreSupport<K, V> {
    Map<K, V> getAll(Connection conn, Collection<K> keys, boolean locked) throws SQLException;

    V get(Connection conn, K key, boolean locked) throws SQLException;

    void putAll(Connection conn, Map<K, V> entries, long nowMs) throws SQLException;

    void put(Connection conn, K key, V value, long nowMs) throws SQLException;

    Map<K, V> computeAll(Connection conn, Collection<K> keys, UnaryOperator<V> operator, long nowMs)
            throws SQLException;

    V compute(Connection conn, K key, UnaryOperator<V> operator, long nowMs) throws SQLException;

    Set<K> removeAll(Connection conn, Collection<K> keys, long nowMs) throws SQLException;

    boolean remove(Connection conn, K key, long nowMs) throws SQLException;
}
