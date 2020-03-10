package org.dru.dusap.database.model;

import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DbTable<T> extends DbEntity<T> {
    private final DbContext context;
    private final List<DbColumn<?>> columns;
    private final Constructor<T> constructor;

    DbTable(final DbContext context, final String name, final Class<T> type) {
        super(name, type);
        Objects.requireNonNull(context, "context");
        this.context = context;
        columns = new ArrayList<>();
        if (type != null) {
            constructor = ReflectionUtils.getDefaultConstructor(type);
            ReflectionUtils.getSerializableFields(type).forEach(field ->
                    addColumn(new DbColumn<>(this, field.getName(), field.getType(), field)));
        } else {
            constructor = null;
        }
    }

    @Override
    public DbContext getContext() {
        return context;
    }

    @Override
    public <R, D> R accept(final DbEntityVisitor<R, D> visitor, final D data) {
        return visitor.visitTable(this, data);
    }

    public int getColumnCount() {
        return columns.size();
    }

    public List<DbColumn<?>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public <C> DbColumn<C> newColumn(final String name, final Class<C> type) {
        final DbColumn<C> column = new DbColumn<>(this, name, type, null);
        addColumn(column);
        return column;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public T getResult(final ResultSet rset) throws SQLException {
        final T value = ReflectionUtils.newInstance(getConstructor());
        int index = 0;
        for (final DbColumn<?> column : getColumns()) {
            column.getResultInto(rset, ++index, value);
        }
        return value;
    }

    public void setParameter(final PreparedStatement stmt, final T value) throws SQLException {
        int index = 0;
        for (final DbColumn<?> column : getColumns()) {
            column.setParameterFrom(stmt, ++index, value);
        }
    }

    private void addColumn(final DbColumn<?> column) {
        Objects.requireNonNull(column, "column");
        columns.add(column);
    }
}
