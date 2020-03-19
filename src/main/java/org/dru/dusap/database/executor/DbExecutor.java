package org.dru.dusap.database.executor;

import org.dru.dusap.functional.ThrowingConsumer;
import org.dru.dusap.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbExecutor {
    <T> T invoke(ThrowingFunction<Connection, T, SQLException> command);

    void execute(ThrowingConsumer<Connection, SQLException> command);

    void beginTransaction();

    void commit();

    void rollback();
}
