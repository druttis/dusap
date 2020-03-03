package org.dru.dusap.database.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class DbCommand {
    private final List<DbMember<?>> fields;
    private final DbTable<?> table;
    private final Map<DbMember<?>, Integer> indexByMember;
    private final int nextIndex;
    protected String sql;

    protected DbCommand(final List<DbMember<?>> fields, final DbTable<?> table) {
        Objects.requireNonNull(fields, "columns");
        Objects.requireNonNull(table, "table");
        this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
        this.table = table;
        indexByMember = new HashMap<>();
        int index = 1;
        for (final DbMember<?> field : fields) {
            indexByMember.put(field, index);
            index += field.getColumnCount();
        }
        nextIndex = index;
    }

    public final List<DbMember<?>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public final DbTable<?> getTable() {
        return table;
    }

    public final int getFieldIndex(final DbMember<?> field) {
        Objects.requireNonNull(field, "field");
        final int index = indexByMember.getOrDefault(field, -1);
        if (index == -1) {
            throw new IllegalArgumentException("no such field: name=" + field);
        }
        return index;
    }

    public final int getNextIndex() {
        return nextIndex;
    }

    public final String getSQL() {
        if (sql == null) {
            sql = createSQL();
        }
        return sql;
    }

    public final PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(getSQL());
    }

    public final <T> T getField(final ResultSet rset, final DbMember<T> field) throws SQLException {
        return field.getResult(rset, getFieldIndex(field));
    }

    public final <T> void setField(final PreparedStatement stmt, final DbMember<T> field, final T value)
            throws SQLException {
        field.setParameter(stmt, getFieldIndex(field), value);
    }

    protected abstract String createSQL();

    public static abstract class Builder {
        private final List<DbMember<?>> fields;
        private DbTable<?> table;

        protected Builder() {
            fields = new ArrayList<>();
        }

        protected final List<DbMember<?>> getFields() {
            return Collections.unmodifiableList(fields);
        }

        protected final DbTable<?> getTable() {
            return table;
        }

        protected final void addField(final DbMember<?> field) {
            Objects.requireNonNull(field, "field");
            for (final DbMember<?> f : fields) {
                if (f.getName().equals(field.getName())) {
                    throw new IllegalArgumentException("duplicate field: name=" + field.getName());
                }
            }
            fields.add(field);
            addFieldTable(field);
        }

        protected final void addFieldTable(final DbMember<?> field) {
            Objects.requireNonNull(field, "field");
            if (table == null) {
                table = field.getTable();
            } else if (!table.equals(field.getTable())) {
                throw new IllegalArgumentException("multiple tables not supported");
            }
        }
    }
}
