package org.dru.dusap.database.model;

import org.dru.dusap.database.executor.DbExecutor;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DbTableManagerImpl implements DbTableManager {
    private final Set<Entry> visited;

    public DbTableManagerImpl() {
        visited = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void createTableIfNotExist(final DbExecutor executor, final int shard, final DbTable<?> table) {
        if (visited.add(new Entry(executor, shard))) {
            executor.execute(connection -> {
                final String ddl = table.accept(DDLEntityVisitor.INSTANCE, null);
                try (final PreparedStatement stmt = connection.prepareStatement(ddl)) {
                    stmt.execute();
                }
            });
        }
    }

    private static final class Entry {
        private final DbExecutor executor;
        private final int shard;

        private Entry(final DbExecutor executor, final int shard) {
            this.executor = executor;
            this.shard = shard;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;
            final Entry entry = (Entry) o;
            return shard == entry.shard &&
                    executor.equals(entry.executor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executor, shard);
        }
    }
}
