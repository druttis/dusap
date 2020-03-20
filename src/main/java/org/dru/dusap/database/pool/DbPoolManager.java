package org.dru.dusap.database.pool;

import java.util.List;

public interface DbPoolManager {
    List<DbPool> getPools(String name);

    DbPool getPool(String name, final int bucketNum);
}
