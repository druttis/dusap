package org.dru.dusap.database.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DbTable<T> extends DbEntity<T> implements DbContainer {
    private final DbContext context;
    private final DbSupport support;

    public DbTable(final DbContext context, final String name, final Class<T> type) {
        super(null, name, type);
        Objects.requireNonNull(context, "context");
        this.context = context;
        support = new DbSupport(this);
        if (type != null) {
            support.populateMembers(type);
        }
    }

    @Override
    public DbContext getContext() {
        return context;
    }

    @Override
    public String getQualifiedName(final String name) {
        Objects.requireNonNull(name, "name");
        return name;
    }

    @Override
    public String getDDL() {
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXIST ");
        sb.append(getDbName());
        sb.append(" (\n");
        sb.append(getColumns().stream()
                .map(DbMember::getDDL)
                .collect(Collectors.joining(",\n")));
        final List<DbMember<?>> primaryKeyColumns = getPrimaryKeyColumns();
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
    public DbTable<?> getTable() {
        return this;
    }

    @Override
    public boolean hasMembers() {
        return support.hasMembers();
    }

    @Override
    public List<DbMember<?>> getMembers() {
        return support.getMembers();
    }

    @Override
    public <F> DbMember<F> getMember(final String name) {
        return support.getMember(name);
    }

    @Override
    public <F> DbMember<F> newMember(final String name, final Class<F> type) {
        return support.newMember(name, type);
    }

    @Override
    public List<DbMember<?>> getColumns() {
        return support.getColumns();
    }

    public List<DbMember<?>> getPrimaryKeyColumns() {
        return getColumns().stream()
                .filter(DbMember::isPrimaryKey)
                .collect(Collectors.toList());
    }
}
