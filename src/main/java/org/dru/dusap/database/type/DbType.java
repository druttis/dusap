package org.dru.dusap.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.function.Supplier;

public interface DbType<T> {
    SQLType getSQLType();

    boolean isPrimitive();

    boolean isVariableLength();

    T getResult(ResultSet rset, int index, Supplier<T> supplier) throws SQLException;

    T getResult(ResultSet rset, int index) throws SQLException;

    void setParameter(PreparedStatement stmt, int index, T value) throws SQLException;

    String getDDL(int length);
}
