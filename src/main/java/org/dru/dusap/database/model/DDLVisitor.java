package org.dru.dusap.database.model;

import java.util.List;
import java.util.stream.Collectors;

public enum DDLVisitor implements DbVisitor<String, Void> {
    INSTANCE;

    @Override
    public <T> String visitColumn(final DbColumn<T> column, final Void data) {
        final StringBuilder sb = new StringBuilder(column.getDbName());
        sb.append(' ');
        sb.append(column.getDbType().getDDL(column.getLength()));
        if (column.isNotNull() && !column.isPrimaryKey()) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    @Override
    public <T> String visitTable(final DbTable<T> table, final Void data) {
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXIST ");
        sb.append(table.getDbName());
        sb.append(" (\n");
        sb.append(table.getDbColumns().stream()
                .map(column -> column.accept(this, data))
                .collect(Collectors.joining(",\n")));
        final List<DbColumn<?>> primaryKeyColumns = table.getDbColumns().stream()
                .filter(DbColumn::isPrimaryKey)
                .collect(Collectors.toList());
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
