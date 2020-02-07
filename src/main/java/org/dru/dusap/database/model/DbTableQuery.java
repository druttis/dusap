package org.dru.dusap.database.model;

import org.dru.dusap.util.Tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

public interface DbTableQuery<T> {
    T getOne(Connection conn, Object... primaryKeys) throws SQLException;

    List<T> getAll(Connection conn, String conditions, Object... params) throws SQLException;

    List<T> getAll(Connection conn, int limit, int offset) throws SQLException;

    List<T> getAll(Connection conn, int limit) throws SQLException;

    List<T> getAll(Connection conn) throws SQLException;

    void insertOne(Connection conn, T object) throws SQLException;

    void insertAll(Connection conn, Collection<T> objects) throws SQLException;

    void setOne(Connection conn, T object) throws SQLException;

    void setAll(Connection conn, Collection<T> objects) throws SQLException;

    Tuple<T, T> updateOne(Connection conn, UnaryOperator<T> operator, String conditions, Object... params)
            throws SQLException;

    List<Tuple<T, T>> updateAll(Connection conn, UnaryOperator<T> operator, String conditions, Object... params)
            throws SQLException;

    void replaceOne(Connection conn, Tuple<T, T> tuple) throws SQLException;

    void replaceAll(Connection conn, List<Tuple<T, T>> tuples) throws SQLException;

    void deleteOne(Connection conn, Object... primaryKeys) throws SQLException;

    void deleteAll(Connection conn, String conditions, Object... params) throws SQLException;

    void removeOne(Connection conn, T object) throws SQLException;

    void removeAll(Connection conn, Collection<T> objects) throws SQLException;
}
