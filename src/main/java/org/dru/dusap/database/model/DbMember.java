package org.dru.dusap.database.model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class DbMember {
    private final Field field;
    private DbContainer parent;

    DbMember(final Field field) {
        this.field = field;
    }

    public final String getName() {
        return getField().getName();
    }

    public final String getQName() {
        final DbContainer parent = getParent();
        return (parent != null ? parent.getQName(getName()) : getName());
    }

    public final String getDbName() {
        return String.format("`%s`", getQName());
    }

    public final DbContainer getParent() {
        return parent;
    }

    final void setParent(final DbContainer parent) {
        this.parent = parent;
    }

    final Field getField() {
        return field;
    }

    abstract int getColumnCount();

    abstract void fetchResult(final Object object, final ResultSet rset, final int index) throws SQLException;
}
