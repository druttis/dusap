package org.dru.dusap.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public interface DbType<T> {
    SQLType getSQLType();

    boolean isVariableLength();

    T getResult(ResultSet rset, int index, T defaultValue) throws SQLException;

    T getResult(ResultSet rset, int index) throws SQLException;

    void setParameter(PreparedStatement stmt, int index, T value) throws SQLException;

    String getDDL(int length);
}
