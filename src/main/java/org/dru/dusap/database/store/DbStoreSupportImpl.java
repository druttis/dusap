package org.dru.dusap.database.store;

import org.dru.dusap.database.model.DbColumn;
import org.dru.dusap.database.model.DbStatement;
import org.dru.dusap.database.model.DbTable;
import org.dru.dusap.database.model.DbTableFactory;
import org.dru.dusap.functional.ThrowingBiConsumer;
import org.dru.dusap.functional.ThrowingConsumer;
import org.dru.dusap.reflection.ReflectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.UnaryOperator;

public final class DbStoreSupportImpl<K, V> implements DbStoreSupport<K, V> {
    private final DbTable<?> dbTable;
    private final DbColumn<K> dbKey;
    private final DbColumn<V> dbValue;
    private final boolean strict;
    private final DbColumn<Long> dbCreated;
    private final DbColumn<Long> dbModified;
    private final DbStatement dbSelect;
    private final DbStatement dbSelectForUpdate;
    private final DbStatement dbUpsert;
    private final DbStatement dbDelete;
    private final DbStatement dbUpdate;

    public DbStoreSupportImpl(final DbTableFactory dbTableFactory, final String name, final Class<K> keyType,
                              final Class<V> valueType, final boolean strict) {
        dbTable = dbTableFactory.newTable(name);
        dbKey = dbTable.newColumn("key", keyType).primaryKey();
        dbValue = dbTable.newColumn("value", valueType).length(65536);
        this.strict = strict;
        dbCreated = dbTable.newColumn("created", Long.class);
        dbModified = dbTable.newColumn("modified", Long.class);
        dbSelect = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2", dbValue, dbTable, dbKey);
        dbSelectForUpdate = DbStatement.parse("SELECT %r FROM %n WHERE %p=? LIMIT 2 FOR UPDATE", dbValue, dbTable, dbKey);
        dbUpsert = DbStatement.parse("INSERT INTO %n (%p,%p,%p) VALUES (?,?,?) ON DUPLICATE KEY UPDATE %2=VALUES(%2),%n=VALUES(%3)", dbTable, dbKey, dbValue, dbCreated, dbModified);
        dbDelete = DbStatement.parse("DELETE FROM %n WHERE %p=?", dbTable, dbKey);
        dbUpdate = DbStatement.parse("UPDATE %n SET %p=?,%p=? WHERE %p=?", dbTable, dbValue, dbModified, dbKey);
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    @Override
    public Map<K, V> getAll(final Connection conn, final Collection<K> keys, final boolean locked) throws SQLException {
        final Map<K, V> entries;
        if (keys.isEmpty()) {
            entries = Collections.emptyMap();
        } else if (keys.size() == 1) {
            final K key = keys.iterator().next();
            final DbStatement dbStatement = (locked ? dbSelectForUpdate : dbSelect);
            try (final PreparedStatement stmt = dbStatement.prepareStatement(conn)) {
                dbStatement.setParameter(stmt, dbKey, key);
                stmt.setFetchSize(2);
                try (final ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        final V value = dbStatement.getResult(rset, dbValue);
                        if (value != null) {
                            entries = Collections.singletonMap(key, value);
                        } else {
                            entries = Collections.emptyMap();
                        }
                        if (rset.next()) {
                            throw new SQLException("duplicate key: " + key);
                        }
                    } else {
                        entries = Collections.emptyMap();
                    }
                }
            }
        } else {
            entries = new HashMap<>();
            final DbStatement dbStatement = DbStatement.parse("SELECT %r,%r FROM %n WHERE %n IN (%L) LIMIT %d"
                    + (locked ? " FOR UPDATE" : ""), dbKey, dbValue, dbTable, dbKey, keys.size(), keys.size() + 1);
            try (final PreparedStatement stmt = dbStatement.prepareStatement(conn)) {
                dbStatement.setParameters(stmt, dbKey, keys);
                stmt.setFetchSize(keys.size() + 1);
                try (final ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        final K key = dbStatement.getResult(rset, dbKey);
                        final V value = dbStatement.getResult(rset, dbValue);
                        if (value != null) {
                            if (entries.put(key, value) != null) {
                                throw new SQLException("duplicate key: " + key);
                            }
                        }
                    }
                    if (rset.next()) {
                        final K key = dbStatement.getResult(rset, dbKey);
                        throw new SQLException("duplicate key: " + key);
                    }
                }
            }
        }
        return entries;
    }

    @Override
    public V get(final Connection conn, final K key, final boolean locked) throws SQLException {
        return getAll(conn, Collections.singleton(key), locked).get(key);
    }

    @Override
    public void putAll(final Connection conn, final Map<K, V> entries, final long nowMs) throws SQLException {
        if (entries.isEmpty()) {
            return;
        }
        final Set<K> removeKeys = new HashSet<>();
        try (final PreparedStatement stmt = dbUpsert.prepareStatement(conn)) {
            entries.forEach(ThrowingBiConsumer.wrap((key, value) -> {
                if (strict && value == null) {
                    removeKeys.add(key);
                } else {
                    dbUpsert.setParameter(stmt, dbKey, key);
                    dbUpsert.setParameter(stmt, dbValue, value);
                    dbUpsert.setParameter(stmt, dbCreated, nowMs);
                    stmt.addBatch();
                }
            }));
            stmt.executeBatch();
        }
        removeAll(conn, removeKeys, nowMs);
    }

    @Override
    public void put(final Connection conn, final K key, final V value, final long nowMs) throws SQLException {
        putAll(conn, Collections.singletonMap(key, value), nowMs);
    }

    @Override
    public Map<K, V> computeAll(final Connection conn, final Collection<K> keys, final UnaryOperator<V> operator,
                                final long nowMs) throws SQLException {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<K, V> newEntries = new HashMap<>();
        final Map<K, V> oldEntries = getAll(conn, keys, true);
        final Map<K, V> putEntries = new HashMap<>();
        final Set<K> removeKeys = new HashSet<>();
        for (final K key : keys) {
            final V oldValue = oldEntries.get(key);
            final V newValue = operator.apply(ReflectionUtils.copyInstance(oldValue));
            if (newValue != null) {
                newEntries.put(key, newValue);
                if (!newValue.equals(oldValue)) {
                    putEntries.put(key, newValue);
                }
            } else if (oldValue != null) {
                removeKeys.add(key);
            }
        }
        putAll(conn, putEntries, nowMs);
        removeAll(conn, removeKeys, nowMs);
        return newEntries;
    }

    @Override
    public V compute(final Connection conn, final K key, final UnaryOperator<V> operator, final long nowMs)
            throws SQLException {
        return computeAll(conn, Collections.singleton(key), operator, nowMs).get(key);
    }

    @Override
    public Set<K> removeAll(final Connection conn, final Collection<K> keys, final long nowMs) throws SQLException {
        if (keys.isEmpty()) {
            return Collections.emptySet();
        }
        final int[] array;
        if (strict) {
            try (final PreparedStatement stmt = dbDelete.prepareStatement(conn)) {
                keys.forEach(ThrowingConsumer.wrap(key -> {
                    dbDelete.setParameter(stmt, dbKey, key);
                    stmt.addBatch();
                }));
                array = stmt.executeBatch();
            }
        } else {
            try (final PreparedStatement stmt = dbUpdate.prepareStatement(conn)) {
                keys.forEach(ThrowingConsumer.wrap(key -> {
                    dbUpdate.setParameter(stmt, dbValue, null);
                    dbUpdate.setParameter(stmt, dbModified, nowMs);
                    dbUpdate.setParameter(stmt, dbKey, key);
                    stmt.addBatch();
                }));
                array = stmt.executeBatch();
            }
        }
        final Set<K> removedKeys = new HashSet<>();
        int index = 0;
        keys.forEach(key -> {
            if (array[index] > 0) {
                removedKeys.add(key);
            }
        });
        return removedKeys;
    }

    @Override
    public boolean remove(final Connection conn, final K key, final long nowMs) throws SQLException {
        return removeAll(conn, Collections.singleton(key), nowMs).contains(key);
    }
}
