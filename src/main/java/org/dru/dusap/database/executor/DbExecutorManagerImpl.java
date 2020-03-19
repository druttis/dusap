package org.dru.dusap.database.executor;

import org.dru.dusap.database.pool.DbPoolManager;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class DbExecutorManagerImpl implements DbExecutorManager {
    private final DbPoolManager dbPoolManager;
    private final Map<NameAndShardNum, DbExecutor> executorByNameAndShardNum;

    public DbExecutorManagerImpl(final DbPoolManager dbPoolManager) {
        this.dbPoolManager = dbPoolManager;
        executorByNameAndShardNum = new ConcurrentHashMap<>();
    }

    @Override
    public DbExecutor getExecutor(final String name, final int shardNum) {
        final NameAndShardNum nameAndShardNum = new NameAndShardNum(name, shardNum);
        return executorByNameAndShardNum.computeIfAbsent(nameAndShardNum, $ ->
                new DbExecutorImpl(dbPoolManager.getPool(name, shardNum)));
    }

    @Override
    public DbExecutor getExecutor(final String name) {
        return getExecutor(name, 0);
    }

    private static final class NameAndShardNum {
        private final String name;
        private final int shardNum;

        private NameAndShardNum(final String name, final int shardNum) {
            Objects.requireNonNull(name, "name");
            if (shardNum < 0) {
                throw new IllegalArgumentException("negative shardNum: " + shardNum);
            }
            this.name = name;
            this.shardNum = shardNum;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof NameAndShardNum)) return false;
            final NameAndShardNum that = (NameAndShardNum) o;
            return shardNum == that.shardNum &&
                    name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, shardNum);
        }
    }
}
