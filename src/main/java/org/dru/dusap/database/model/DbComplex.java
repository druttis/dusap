package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class DbComplex extends DbMember implements DbContainer {
    private final DbContainerSupport containerSupport;
    private final Constructor<?> constructor;

    DbComplex(final Field field) {
        super(field);
        constructor = ReflectionUtils.getDefaultConstructor(field.getType());
        containerSupport = new DbContainerSupport(this);
    }

    @Override
    public List<DbMember> getMembers() {
        return containerSupport.getMembers();
    }

    @Override
    public List<DbColumn> getColumns() {
        return containerSupport.getColumns();
    }

    @Override
    public void addMember(final DbMember member) {
        containerSupport.addMember(member);
    }

    @Override
    public void replaceColumnWithComplex(final DbColumn column, final DbComplex complex) {
        containerSupport.replaceColumnWithComplex(column, complex);
    }

    @Override
    public String getQName(final String childName) {
        return String.format("%s_%s", getQName(), childName);
    }

    @Override
    int getColumnCount() {
        return getMembers().stream().mapToInt(DbMember::getColumnCount).sum();
    }

    @Override
    void fetchResult(final Object object, final ResultSet rset, final int index) throws SQLException {
        final Object value = ReflectionUtils.newInstance(constructor);
        int localIndex = index;
        for (final DbMember member : getMembers()) {
            member.fetchResult(value, rset, localIndex);
            localIndex += member.getColumnCount();
        }
        ReflectionUtils.setField(object, getField(), value);
    }
}
