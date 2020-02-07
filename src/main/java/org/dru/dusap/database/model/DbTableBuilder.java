package org.dru.dusap.database.model;

import org.dru.dusap.database.type.DbTypes;
import org.dru.dusap.reflection.ReflectionUtils;

import java.lang.reflect.Field;

public final class DbTableBuilder<T> {
    private final DbTypes dbTypes;
    private final String name;
    private final Class<T> type;
    private final DbBody body;
    private boolean done;

    DbTableBuilder(final DbTypes dbTypes, final String name, final Class<T> type) {
        this.dbTypes = dbTypes;
        this.name = name;
        this.type = type;
        body = new DbBody();
        populateColumns(body, type);
    }

    public DbTableBuilder<T> setLength(final String qname, final int length) {
        checkNotDone();
        body.getColumn(qname).setLength(length);
        return this;
    }

    public DbTableBuilder<T> setRequired(final String qname) {
        checkNotDone();
        body.getColumn(qname).setRequired(true);
        return this;
    }

    public DbTableBuilder<T> setPrimaryKey(final String qname) {
        checkNotDone();
        body.getColumn(qname).setPrimaryKey(true);
        return this;
    }

    public DbTableBuilder<T> setDefaultValue(final String qname, final Object value) {
        checkNotDone();
        body.getColumn(qname).setDefaultValue(value);
        return this;
    }

    public DbTableBuilder<T> flatten(final String qname, final boolean recursive) {
        checkNotDone();
        flatten(body.getColumn(qname), recursive);
        return this;
    }

    public DbTableBuilder<T> flatten(final String qname) {
        return flatten(qname, false);
    }

    public DbTableBuilder<T> flatten(final boolean recursive) {
        checkNotDone();
        body.getColumns().forEach(column -> flatten(column, recursive));
        return this;
    }

    public DbTableBuilder<T> flatten() {
        return flatten(false);
    }

    public DbTable<T> build() {
        checkNotDone();
        done = true;
        return new DbTable<>(name, type, body);
    }

    private void checkNotDone() {
        if (done) {
            throw new IllegalStateException("done");
        }
    }

    private void flatten(final DbColumn column, final boolean recursive) {
        final DbContainer container = column.getParent();
        final Field field = column.getField();
        final Class<?> type = field.getType();
        final DbComplex complex = new DbComplex(field);
        populateColumns(complex, type);
        complex.getColumns().forEach(childColumn -> {
            childColumn.setRequired(column.isRequired());
            childColumn.setPrimaryKey(column.isPrimaryKey());
        });
        container.replaceColumnWithComplex(column, complex);
        if (recursive) {
            complex.getColumns().forEach(childColumn -> flatten(childColumn, true));
        }
    }

    private void populateColumns(final DbContainer container, final Class<?> type) {
        ReflectionUtils.getSerializableFields(type).forEach(field -> {
            container.addMember(new DbColumn(field, dbTypes.getDefaultDbType(field.getType())));
        });
    }
}
