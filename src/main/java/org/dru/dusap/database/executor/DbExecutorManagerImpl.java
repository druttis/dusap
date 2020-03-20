package org.dru.dusap.database.executor;

import org.dru.dusap.database.pool.DbPoolManager;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class DbExecutorManagerImpl implements DbExecutorManager {
    private final DbPoolManager dbPoolManager;
    private final Map<Key, DbExecutor> executorByKey;

    public DbExecutorManagerImpl(final DbPoolManager dbPoolManager) {
        this.dbPoolManager = dbPoolManager;
        executorByKey = new ConcurrentHashMap<>();
    }

    @Override
    public DbExecutor getExecutor(final String name, final int bucketNum) {
        final Key key = new Key(name, bucketNum);
        return executorByKey.computeIfAbsent(key, $ -> new DbExecutorImpl(dbPoolManager.getPool(name, bucketNum)));
    }

    @Override
    public DbExecutor getExecutor(final String name) {
        return getExecutor(name, 0);
    }

    private static final class Key {
        private final String name;
        private final int shardNum;

        private Key(final String name, final int shardNum) {
            Objects.requireNonNull(name, "name");
            if (shardNum < 0) {
                throw new IllegalArgumentException("negative bucketNum: " + shardNum);
            }
            this.name = name;
            this.shardNum = shardNum;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            final Key that = (Key) o;
            return shardNum == that.shardNum &&
                    name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, shardNum);
        }
    }
}
