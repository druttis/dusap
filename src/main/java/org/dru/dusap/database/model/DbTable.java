package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public final class DbTable<T> {
    private final String name;
    private final Constructor<T> constructor;
    private final DbBody body;

    DbTable(final String name, final Class<T> type, final DbBody body) {
        this.name = name;
        constructor = ReflectionUtils.getDefaultConstructor(type);
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getDbName() {
        return String.format("`%s`", getName());
    }

    private List<DbMember> getMembers() {
        return body.getMembers();
    }

    public List<DbColumn> getColumns() {
        return body.getColumns();
    }

    public DbColumn getColumn(final String qname) {
        return body.getColumn(name);
    }

    public T getResult(final ResultSet rset) throws SQLException {
        final T object = ReflectionUtils.newInstance(constructor);
        int index = 1;
        for (final DbMember member : getMembers()) {
            member.fetchResult(object, rset, index);
            index += member.getColumnCount();
        }
        return object;
    }

    public List<DbColumn> getPrimaryKeyColumns() {
        return getColumns().stream()
                .filter(DbColumn::isPrimaryKey)
                .collect(Collectors.toList());
    }

    public List<DbColumn> getNonPrimaryKeyColumns() {
        return getColumns().stream()
                .filter(column -> !column.isPrimaryKey())
                .collect(Collectors.toList());
    }

    public String getDDL() {
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXIST ");
        sb.append(getDbName());
        sb.append(" (\n");
        sb.append(getColumns().stream()
                .map(DbColumn::getDDL)
                .collect(Collectors.joining(",\n")));
        final List<DbColumn> primaryKeyColumns = getPrimaryKeyColumns();
        if (!primaryKeyColumns.isEmpty()) {
            sb.append(",\nPRIMARY KEY (");
            sb.append(primaryKeyColumns.stream()
                    .map(DbColumn::getDbName)
                    .collect(Collectors.joining(",")));
            sb.append(')');
        }
        sb.append("\n) ENGINE=InnoDb CHARACTER SET=utf8mb4");
        return sb.toString();
    }
}
