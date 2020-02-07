package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbType;
import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbColumn extends DbMember {
    private final DbType dbType;
    private int length;
    private boolean required;
    private boolean primaryKey;
    private Object defaultValue;

    DbColumn(final Field field, final DbType dbType) {
        super(field);
        this.dbType = dbType;
    }

    @Override
    int getColumnCount() {
        return 1;
    }

    @Override
    void fetchResult(final Object object, final ResultSet rset, final int index) throws SQLException {
        ReflectionUtils.setField(object, getField(), dbType.getResult(rset, index, defaultValue));
    }

    public int getLength() {
        return length;
    }

    void setLength(final int length) {
        this.length = length;
    }

    public boolean isRequired() {
        return required || primaryKey;
    }

    void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    void setPrimaryKey(final boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    void setDefaultValue(final Object value) {
        this.defaultValue = value;
    }

    String getDDL() {
        final StringBuilder sb = new StringBuilder(getDbName());
        sb.append(' ');
        sb.append(dbType.getDDL(length));
        if (required && !primaryKey) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }
}
