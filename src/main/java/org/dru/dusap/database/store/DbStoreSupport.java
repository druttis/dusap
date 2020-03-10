package org.dru.dusap.database.store;

import org.dru.dusap.database.model.*;
import org.dru.dusap.reflection.ReflectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

public final class DbStoreSupport<K, V> {
    private final DbTable<?> dbTable;
    private final DbColumn<K> dbKey;
    private final DbColumn<V> dbValue;
    private final DbColumn<Long> dbCreated;
    private final DbColumn<Long> dbLastModified;
    private final DbSelect dbSelect;
    private final DbSelect dbSelectForUpdate;
    private final DbInsertOrUpdate dbSet;
    private final DbUpdate dbUpdate;
    private final DbDelete dbDelete;

    public DbStoreSupport(final DbTableFactory dbTableFactory, final String tableName, final Class<K> keyType,
                          final Class<V> valueType) {
        dbTable = dbTableFactory.newTable(tableName);
        dbKey = dbTable.newColumn("key", keyType);
        dbValue = dbTable.newColumn("value", valueType).length(65536);
        dbCreated = dbTable.newColumn("created", Long.class);
        dbLastModified = dbTable.newColumn("lastModified", Long.class);
        dbSelect = DbSelect.column(dbValue).where(dbKey, "=").limit(2);
        dbSelectForUpdate = DbSelect.column(dbValue).where(dbKey, "=").limit(2).forUpdate();
        dbSet = DbInsertOrUpdate.fields(dbKey, dbValue, dbCreated)
                .onDuplicateKeyCopy(dbValue)
                .onDuplicateKeyUpdate(dbLastModified)
                .build();
        dbUpdate = DbUpdate.fields(dbValue, dbLastModified).where(dbKey, "=?").build();
        dbDelete = DbDelete.where(dbKey, "=?").build();
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public V get(final Connection conn, final K key) throws SQLException {
        return get(conn, dbSelect, key, true);
    }

    public void set(final Connection conn, final K key, final V value, final long nowMs) throws SQLException {
        try (final PreparedStatement stmt = dbSet.prepareStatement(conn)) {
            dbSet.setField(stmt, dbKey, key);
            dbSet.setField(stmt, dbValue, value);
            dbSet.setField(stmt, dbCreated, nowMs);
            dbSet.setOnDuplicateKeyUpdate(stmt, dbLastModified, nowMs);
            stmt.executeUpdate();
        }
    }

    public V update(final Connection conn, final K key, final UnaryOperator<V> operator, final long nowMs)
            throws SQLException {
        final V oldValue = get(conn, dbSelectForUpdate, key, true);
        final V newValue = operator.apply(ReflectionUtils.copyInstance(oldValue));
        if ((newValue == null && oldValue != null) || (newValue != null && !newValue.equals(oldValue))) {
            if (newValue == null) {
                delete(conn, key);
            } else if (oldValue == null) {
                set(conn, key, newValue, nowMs);
            } else {
                try (final PreparedStatement stmt = dbUpdate.prepareStatement(conn)) {
                    dbUpdate.setField(stmt, dbValue, newValue);
                    dbUpdate.setField(stmt, dbLastModified, nowMs);
                    dbUpdate.setCondition(stmt, dbKey, key);
                    stmt.executeUpdate();
                }
            }
        }
        return newValue;
    }

    public boolean delete(final Connection conn, final K key) throws SQLException {
        try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
            dbDelete.setCondition(stmt, dbKey, key);
            return (stmt.executeUpdate() != 0);
        }
    }

    private V get(final Connection conn, final DbSelect select, final K key, final boolean checkDuplicate)
            throws SQLException {
        try (final PreparedStatement stmt = select.prepareStatement(conn)) {
            select.setParameter(stmt, dbKey, key);
            try (final ResultSet rset = stmt.executeQuery()) {
                if (select.getLimit() != null) {
                    rset.setFetchSize(select.getLimit());
                }
                if (!rset.next()) {
                    return null;
                }
                final V value = select.getResult(rset, dbValue);
                if (checkDuplicate && rset.next()) {
                    throw new SQLException("duplicate: key=" + key);
                }
                return value;
            }
        }
    }
}
