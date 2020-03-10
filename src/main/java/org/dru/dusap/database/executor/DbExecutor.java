package org.dru.dusap.database.executor;

import org.dru.dusap.functional.ThrowingConsumer;
import org.dru.dusap.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbExecutor {
    <T> T query(int shard, ThrowingFunction<Connection, T, SQLException> command) throws SQLException;

    <T> T update(int shard, ThrowingFunction<Connection, T, SQLException> command) throws SQLException;

    void execute(int shard, ThrowingConsumer<Connection, SQLException> command) throws SQLException;
}
