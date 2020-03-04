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
    private final DbMember<K> dbKey;
    private final DbMember<V> dbValue;
    private final DbMember<Long> dbCreated;
    private final DbMember<Long> dbLastModified;
    private final DbSelect dbSelect;
    private final DbSelect forUpdate;
    private final DbInsert dbInsert;
    private final DbUpdate dbUpdate;
    private final DbDelete dbDelete;

    DbStoreImpl(final DbExecutor dbExecutor, final TimeSupplier timeSupplier, final String name, final Class<K> keyType,
                final Class<V> valueType, final DbTableFactory dbTableFactory, final DbTableManager tableManager,
                final boolean exploded) {
        this.dbExecutor = dbExecutor;
        this.timeSupplier = timeSupplier;
        final DbTable<?> dbTable = dbTableFactory.newTable(name);
        dbKey = dbTable.newMember("key", keyType);
        dbValue = dbTable.newMember("value", valueType).length(256);
        if (exploded) {
            dbValue.explode(true);
        }
        dbCreated = dbTable.newMember("created", Long.class);
        dbLastModified = dbTable.newMember("lastModified", Long.class);
        dbSelect = DbSelect.field(dbValue).where(dbKey, "=?").limit(2).build();
        forUpdate = DbSelect.extend(dbSelect).forUpdate().build();
        dbInsert = DbInsert.fields(dbKey, dbValue, dbCreated).build();
        dbUpdate = DbUpdate.fields(dbValue, dbLastModified).where(dbKey, "=?").build();
        dbDelete = DbDelete.where(dbKey, "=?").build();
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
    public V update(final K key, final UnaryOperator<V> updater) {
        try {
            return dbExecutor.update(0, conn -> {
                final V original = get(conn, forUpdate, key);
                final V result = updater.apply(ReflectionUtils.copyInstance(original));
                if ((original == null && result != null) || (original != null && !original.equals(result))) {
                    if (original == null) {
                        try (final PreparedStatement stmt = dbInsert.prepareStatement(conn)) {
                            dbInsert.setField(stmt, dbKey, key);
                            dbInsert.setField(stmt, dbValue, result);
                            dbInsert.setField(stmt, dbCreated, timeSupplier.get().toEpochMilli());
                            stmt.executeUpdate();
                        }
                    } else if (result == null) {
                        delete(conn, key);
                    } else {
                        try (final PreparedStatement stmt = dbUpdate.prepareStatement(conn)) {
                            dbUpdate.setField(stmt, dbValue, result);
                            dbUpdate.setField(stmt, dbLastModified, timeSupplier.get().toEpochMilli());
                            dbUpdate.setCondition(stmt, dbKey, key);
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
    public void delete(final K key) {
        try {
            dbExecutor.update(0, conn -> {
                delete(conn, key);
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    private V get(final Connection conn, final DbSelect select, final K key) throws SQLException {
        try (final PreparedStatement stmt = select.prepareStatement(conn)) {
            select.setCondition(stmt, dbKey, key);
            try (final ResultSet rset = stmt.executeQuery()) {
                rset.setFetchSize(2);
                if (!rset.next()) {
                    return null;
                }
                final V value = select.getField(rset, dbValue);
                if (rset.next()) {
                    throw new SQLException("duplicate: key=" + key);
                }
                return value;
            }
        }
    }

    private void delete(final Connection conn, final K key) throws SQLException {
        try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
            dbDelete.setCondition(stmt, dbKey, key);
            stmt.executeUpdate();
        }
    }
}
