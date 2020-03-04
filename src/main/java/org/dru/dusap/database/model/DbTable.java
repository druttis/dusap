package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DbTable<T> extends DbEntity<T> {
    private final Constructor<T> constructor;

    DbTable(final DbContext context, final String name, final Class<T> type) {
        super(context, name, type);
        if (type != null) {
            constructor = ReflectionUtils.getDefaultConstructor(type);
            ReflectionUtils.getSerializableFields(type).forEach(field ->
                    addMember(new DbMember<>(getContext(), this, this, field.getName(), field.getType(), field)));
        } else {
            constructor = null;
        }
    }

    @Override
    public DbTable<?> getTable() {
        return this;
    }

    @Override
    public final List<DbMember<?>> getColumns() {
        return getMembers().stream()
                .flatMap(member -> member.getColumns().stream())
                .collect(Collectors.toList());
    }

    @Override
    public T getResult(final ResultSet rset, final int index) throws SQLException {
        final T result = ReflectionUtils.newInstance(getConstructor());
        int local = index;
        for (final DbMember<?> member : getMembers()) {
            final Object value = member.getResult(rset, local);
            ReflectionUtils.setField(result, member.getField(), value);
            local += member.getColumnCount();
        }
        return result;
    }

    @Override
    public String getDDL() {
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(getDbName());
        sb.append(" (\n");
        sb.append(getColumns().stream()
                .map(DbEntity::getDDL)
                .collect(Collectors.joining(",\n")));
        final List<DbMember<?>> primaryKeyColumns = getColumns().stream()
                .filter(DbMember::isPrimaryKey)
                .collect(Collectors.toList());
        if (!primaryKeyColumns.isEmpty()) {
            sb.append(",\nPRIMARY KEY (");
            sb.append(primaryKeyColumns.stream()
                    .map(DbMember::getDbName)
                    .collect(Collectors.joining(",")));
            sb.append(')');
        }
        sb.append("\n) ENGINE=InnoDb CHARACTER SET=utf8mb4");
        return sb.toString();
    }

    @Override
    protected void setParameterRaw(final PreparedStatement stmt, final int index, final Object value)
            throws SQLException {
        if (getType() == null) {
            throw new IllegalStateException("table type not defined");
        }
        setParameterFrom(stmt, index, getType().cast(value));
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public DbTable<T> defaultSupplier(final Supplier<T> supplier) {
        setDefaultSupplier(supplier);
        return this;
    }

    public DbTable<T> defaultValue(final T value) {
        setDefaultValue(value);
        return this;
    }
}
