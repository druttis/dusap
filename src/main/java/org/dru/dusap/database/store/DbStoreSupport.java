package org.dru.dusap.database.store;

import org.dru.dusap.database.model.DbColumn;
import org.dru.dusap.database.model.DbStatement;
import org.dru.dusap.database.model.DbTable;
import org.dru.dusap.database.model.DbTableFactory;
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
    private final DbColumn<Long> dbModified;
    private final DbStatement dbSelect;
    private final DbStatement dbSelectForUpdate;
    private final DbStatement dbUpsert;
    private final DbStatement dbUpdate;
    private final DbStatement dbDelete;

    public DbStoreSupport(final DbTableFactory dbTableFactory, final String tableName, final Class<K> keyType,
                          final Class<V> valueType) {
        dbTable = dbTableFactory.newTable(tableName);
        dbKey = dbTable.newColumn("key", keyType);
        dbValue = dbTable.newColumn("value", valueType).length(65536);
        dbCreated = dbTable.newColumn("created", Long.class);
        dbModified = dbTable.newColumn("lastModified", Long.class);
        dbSelect = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2", dbValue, dbTable, dbKey);
        dbSelectForUpdate = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2 FOR UPDATE", dbValue, dbTable, dbKey);
        dbUpsert = DbStatement.parse("INSERT INTO %n (%p,%p,%p) VALUES (?,?,?) ON DUPLICATE KEY UPDATE %2=VALUES(%2),%n=VALUES(%3)", dbTable, dbKey, dbValue, dbCreated);
        dbUpdate = DbStatement.parse("UPDATE %n SET %p=?,%p=? WHERE %p=?", dbTable, dbValue, dbModified, dbKey);
        dbDelete = DbStatement.parse("DELETE FROM %n WHERE %p=?", dbTable, dbKey);
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public V get(final Connection conn, final K key) throws SQLException {
        return get(conn, dbSelect, key, true);
    }

    public void set(final Connection conn, final K key, final V value, final long nowMs) throws SQLException {
        try (final PreparedStatement stmt = dbUpsert.prepareStatement(conn)) {
            dbUpsert.setParameter(stmt, dbKey, key);
            dbUpsert.setParameter(stmt, dbValue, value);
            dbUpsert.setParameter(stmt, dbCreated, nowMs);
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
                    dbUpdate.setParameter(stmt, dbValue, newValue);
                    dbUpdate.setParameter(stmt, dbModified, nowMs);
                    dbUpdate.setParameter(stmt, dbKey, key);
                    stmt.executeUpdate();
                }
            }
        }
        return newValue;
    }

    public boolean delete(final Connection conn, final K key) throws SQLException {
        try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
            dbDelete.setParameter(stmt, dbKey, key);
            return (stmt.executeUpdate() != 0);
        }
    }

    private V get(final Connection conn, final DbStatement select, final K key, final boolean checkDuplicate)
            throws SQLException {
        try (final PreparedStatement stmt = select.prepareStatement(conn)) {
            select.setParameter(stmt, dbKey, key);
            try (final ResultSet rset = stmt.executeQuery()) {
                if (checkDuplicate) {
                    rset.setFetchSize(2);
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
