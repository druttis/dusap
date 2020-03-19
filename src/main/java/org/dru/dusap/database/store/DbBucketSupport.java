package org.dru.dusap.database.store;

import org.dru.dusap.database.executor.DbExecutor;
import org.dru.dusap.database.model.DbColumn;
import org.dru.dusap.database.model.DbStatement;
import org.dru.dusap.database.model.DbTable;
import org.dru.dusap.database.model.DbTableFactory;
import org.dru.dusap.store.Row;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public final class DbBucketSupport<K, V> {
    private final DbTable<?> dbTable;
    private final DbColumn<K> dbKey;
    private final DbColumn<V> dbValue;
    private final DbColumn<Long> dbModified;
    private final DbStatement dbSelect;
    private final DbStatement dbLock;
    private final DbStatement dbUpsert;
    private final DbStatement dbUpdate;
    private final DbStatement dbDelete;

    public DbBucketSupport(final DbTableFactory dbTableFactory, final String tableName,
                           final Class<K> keyType, final Class<V> valueType) {
        dbTable = dbTableFactory.newTable(tableName);
        dbKey = dbTable.newColumn("key", keyType).primaryKey();
        dbValue = dbTable.newColumn("value", valueType);
        dbModified = dbTable.newColumn("modified", Long.class);
        dbSelect = DbStatement.parse("SELECT %r,%r FROM %n WHERE %p=? LIMIT 2", dbValue, dbModified, dbTable, dbKey);
        dbLock = dbSelect.extend("FOR UPDATE");
        dbUpsert = DbStatement.parse("INSERT INTO %n (%p,%p,%p) VALUES (?,?,?) ON DUPLICATE KEY UPDATE "
                        + "%2=IF(@update:=%3<=VALUES(%3),VALUES(%2),%2),%3=IF(@update, VALUES(%3),%3)",
                dbTable, dbKey, dbValue, dbModified);
        dbUpdate = DbStatement.parse("UPDATE %n SET "
                + "%p=IF(@update:=%2<=VALUES(%2),VALUES(%2),?),%p=IF(@update,VALUES(%3),?)"
                + " WHERE %p=?", dbTable, dbValue, dbModified, dbKey);
        dbDelete = DbStatement.parse("DELETE FROM %n WHERE %p=? AND %p<=?", dbTable, dbKey, dbModified);
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public Map<K, Row<V>> select(final DbExecutor dbExecutor, final Set<K> keys, final boolean lock) {
        Objects.requireNonNull(dbExecutor, "dbExecutor");
        Objects.requireNonNull(keys, "keys");
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        } else if (keys.size() == 1) {
            final K key = keys.iterator().next();
            return Collections.singletonMap(key, select(dbExecutor, key, lock));
        } else {
            return dbExecutor.invoke(conn -> {
                final StringBuilder sb = new StringBuilder("SELECT %r,%r,%r FROM %n WHERE %n IN (%L) LIMIT %d");
                if (lock) {
                    sb.append(" FOR UPDATE");
                }
                final DbStatement dbSelectAll = DbStatement.parse(sb.toString(),
                        dbKey, dbValue, dbModified, dbTable, dbKey, keys.size(), keys.size() + 1);
                try (final PreparedStatement stmt = dbSelectAll.prepareStatement(conn)) {
                    dbSelectAll.setParameters(stmt, dbKey, keys);
                    stmt.setFetchSize(keys.size() + 1);
                    try (final ResultSet rset = stmt.executeQuery()) {
                        final Map<K, Row<V>> rows = new HashMap<>();
                        while (rset.next()) {
                            final K key = dbSelectAll.getResult(rset, dbKey);
                            final V value = dbSelectAll.getResult(rset, dbValue);
                            final long modified = dbSelectAll.getResult(rset, dbModified);
                            if (value != null) {
                                if (rows.put(key, Row.create(value, modified)) != null) {
                                    throw new IllegalStateException("duplicate key: " + key + "(1+)");
                                }
                            }
                        }
                        return rows;
                    }
                }
            });
        }
    }

    public void upsert(final DbExecutor dbExecutor, final K key, final V value, final long modified) {
        Objects.requireNonNull(dbExecutor, "dbExecutor");
        Objects.requireNonNull(key, "key");
        dbExecutor.execute(conn -> {
            try (final PreparedStatement stmt = dbUpsert.prepareStatement(conn)) {
                dbUpsert.setParameter(stmt, dbKey, key);
                dbUpsert.setParameter(stmt, dbValue, value);
                dbUpsert.setParameter(stmt, dbModified, modified);
                stmt.executeUpdate();
            }
        });
    }

    public boolean update(final DbExecutor dbExecutor, final K key, final V value, final long modified) {
        return dbExecutor.invoke(conn -> {
            try (final PreparedStatement stmt = dbUpdate.prepareStatement(conn)) {
                dbUpdate.setParameter(stmt, dbValue, value);
                dbUpdate.setParameter(stmt, dbModified, modified);
                dbUpdate.setParameter(stmt, dbKey, key);
                final int result = stmt.executeUpdate();
                if (result > 1) {
                    throw new RuntimeException("duplicate key: " + key + " (" + result + ")");
                }
                return (result == 1);
            }
        });
    }

    public boolean delete(final DbExecutor dbExecutor, final K key, final long modified) {
        Objects.requireNonNull(dbExecutor, "dbExecutor");
        Objects.requireNonNull(key, "key");
        return dbExecutor.invoke(conn -> {
            try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
                dbDelete.setParameter(stmt, dbKey, key);
                dbDelete.setParameter(stmt, dbModified, modified);
                final int result = stmt.executeUpdate();
                if (result > 1) {
                    throw new InternalError("duplicate key: " + key + " (" + result + ")");
                }
                return (result == 1);
            }
        });
    }

    private Row<V> select(final DbExecutor dbExecutor, final K key, final boolean lock) {
        final DbStatement dbStatement = (lock ? dbLock : dbSelect);
        return dbExecutor.invoke(conn -> {
            try (final PreparedStatement stmt = dbStatement.prepareStatement(conn)) {
                dbStatement.setParameter(stmt, dbKey, key);
                stmt.setFetchSize(2);
                try (final ResultSet rset = stmt.executeQuery()) {
                    if (!rset.next()) {
                        return null;
                    }
                    final V value = dbStatement.getResult(rset, dbValue);
                    final long modified = dbStatement.getResult(rset, dbModified);
                    if (rset.next()) {
                        throw new IllegalStateException("duplicate key: " + key + "(1+)");
                    }
                    return Row.create(value, modified);
                }
            }
        });
    }
}
