package org.dru.dusap.database.model;

import java.util.List;
import java.util.stream.Collectors;

public enum DDLEntityVisitor implements DbEntityVisitor<String, Void> {
    INSTANCE;

    @Override
    public <C> String visitColumn(final DbColumn<C> column, final Void data) {
        final StringBuilder sb = new StringBuilder();
        sb.append(column.getDbName());
        sb.append(" ");
        sb.append(column.getDbType().getDDL(column.getLength()));
        if (column.isNotNull() && !column.isPrimaryKey()) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    @Override
    public <T> String visitTable(final DbTable<T> table, final Void data) {
        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(table.getDbName());
        sb.append(" (\n");
        sb.append(table.getColumns().stream()
                .map(column -> column.accept(this, data))
                .collect(Collectors.joining(",\n")));
        final List<DbColumn<?>> primaryKeys = table.getColumns().stream()
                .filter(DbColumn::isPrimaryKey)
                .collect(Collectors.toList());
        if (!primaryKeys.isEmpty()) {
            sb.append(",\nPRIMARY KEY (");
            sb.append(primaryKeys.stream()
                    .map(DbColumn::getDbName)
                    .collect(Collectors.joining(",")));
            sb.append(")");
        }
        sb.append("\n) ENGINE=InnoDb CHARACTER SET=utf8mb4");
        return sb.toString();
    }
}
