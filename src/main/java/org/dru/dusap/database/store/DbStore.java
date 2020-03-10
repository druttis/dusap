package org.dru.dusap.database.store;

import java.sql.SQLException;
import java.util.function.UnaryOperator;

public interface DbStore<K, V> {
    V get(K key) throws SQLException;

    void set(K key, V value) throws SQLException;

    V update(K key, UnaryOperator<V> updater) throws SQLException;

    boolean delete(K key) throws SQLException;
}
