package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.database.model.*;
import org.dru.dusap.reflection.ReflectionUtils;
import org.dru.dusap.time.TimeSupplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

public final class DbStoreImpl<K, V> implements DbStore<K, V> {
    private final DbExecutor dbExecutor;
    private final TimeSupplier timeSupplier;
    private final DbColumn<K> dbKey;
    private final DbColumn<V> dbValue;
    private final DbColumn<Long> dbCreated;
    private final DbColumn<Long> dbModified;
    private final DbStatement dbSelect;
    private final DbStatement dbSelectForUpdate;
    private final DbStatement dbInsert;
    private final DbStatement dbUpdate;
    private final DbStatement dbDelete;

    DbStoreImpl(final DbExecutor dbExecutor, final TimeSupplier timeSupplier, final String name, final Class<K> keyType,
                final Class<V> valueType, final DbTableFactory dbTableFactory, final DbTableManager tableManager) {
        this.dbExecutor = dbExecutor;
        this.timeSupplier = timeSupplier;
        final DbTable<?> dbTable = dbTableFactory.newTable(name);
        dbKey = dbTable.newColumn("key", keyType);
        dbValue = dbTable.newColumn("value", valueType).length(65536);
        dbCreated = dbTable.newColumn("created", Long.class);
        dbModified = dbTable.newColumn("modified", Long.class);
        dbSelect = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2", dbValue, dbTable, dbKey);
        dbSelectForUpdate = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2 FOR UPDATE", dbValue, dbTable, dbKey);
        dbInsert = DbStatement.parse("INSERT INTO %n (%p,%p,%p) VALUES (?,?,?)", dbTable, dbKey, dbValue, dbCreated);
        dbUpdate = DbStatement.parse("UPDATE %n SET %p=?,%p=? WHERE %p=?", dbTable, dbValue, dbModified, dbKey);
        dbDelete = DbStatement.parse("DELETE FROM %n WHERE %p=?", dbTable, dbKey);
        tableManager.createTableIfNotExist(dbExecutor, 0, dbTable);
    }

    @Override
    public V get(final K key) {
        try {
            return dbExecutor.query(0, conn -> get(conn, dbSelect, key));
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void set(final K key, final V value) throws SQLException {
    }

    @Override
    public V update(final K key, final UnaryOperator<V> updater) {
        try {
            return dbExecutor.update(0, conn -> {
                final V original = get(conn, dbSelectForUpdate, key);
                final V result = updater.apply(ReflectionUtils.copyInstance(original));
                if ((original == null && result != null) || (original != null && !original.equals(result))) {
                    if (original == null) {
                        try (final PreparedStatement stmt = dbInsert.prepareStatement(conn)) {
                            dbInsert.setParameter(stmt, dbKey, key);
                            dbInsert.setParameter(stmt, dbValue, result);
                            dbInsert.setParameter(stmt, dbCreated, timeSupplier.get().toEpochMilli());
                            stmt.executeUpdate();
                        }
                    } else if (result == null) {
                        delete(conn, key);
                    } else {
                        try (final PreparedStatement stmt = dbUpdate.prepareStatement(conn)) {
                            dbUpdate.setParameter(stmt, dbValue, result);
                            dbUpdate.setParameter(stmt, dbModified, timeSupplier.get().toEpochMilli());
                            dbUpdate.setParameter(stmt, dbKey, key);
                            stmt.executeUpdate();
                        }
                    }
                }
                return result;
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public boolean delete(final K key) {
        try {
            return dbExecutor.update(0, conn -> delete(conn, key));
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    private V get(final Connection conn, final DbStatement select, final K key) throws SQLException {
        try (final PreparedStatement stmt = select.prepareStatement(conn)) {
            select.setParameter(stmt, dbKey, key);
            try (final ResultSet rset = stmt.executeQuery()) {
                rset.setFetchSize(2);
                if (!rset.next()) {
                    return null;
                }
                final V value = select.getResult(rset, dbValue);
                if (rset.next()) {
                    throw new SQLException("duplicate: key=" + key);
                }
                return value;
            }
        }
    }

    private boolean delete(final Connection conn, final K key) throws SQLException {
        try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
            dbDelete.setParameter(stmt, dbKey, key);
            return stmt.executeUpdate() != 0;
        }
    }
}
