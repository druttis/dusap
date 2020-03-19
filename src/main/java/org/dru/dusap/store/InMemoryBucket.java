package org.dru.dusap.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryBucket<K, V> extends AbstractBucket<K, V> {
    private final Map<K, Row<V>> rows;
    private final ThreadLocal<Session> sessions;
    private final Map<K, Lock> locks;

    public InMemoryBucket(final int num) {
        super(num);
        rows = new ConcurrentHashMap<>();
        sessions = new ThreadLocal<>();
        locks = new ConcurrentHashMap<>();
    }

    @Override
    public Map<K, Row<V>> select(final Set<K> keys, final boolean lock) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        return session().select(keys, lock);
    }

    @Override
    public void begin() {
        session().begin();
    }

    @Override
    public void upsert(final K key, final V value, final long modified) {
        session().upsert(key, value, modified);
    }

    @Override
    public void update(final K key, final V value, final long modified) {
        session().update(key, value, modified);
    }

    @Override
    public void delete(final K key, final long modified) {
        session().delete(key, modified);
    }

    @Override
    public void commit() {
        session().commit();
    }

    @Override
    public void rollback() {
        session().rollback();
    }

    private Session session() {
        Session session = sessions.get();
        if (session == null) {
            session = new Session();
            sessions.set(session);
        }
        return session;
    }

    private void lock(final K key) {
        locks.computeIfAbsent(key, ($) -> new Lock()).lock();
    }

    private void unlock(final K key) {
        locks.computeIfPresent(key, ($, lock) -> {
            lock.unlock();
            return null;
        });
    }

    private final class Session {
        private final Unmodified unmodified;
        private final Map<K, Modification> modifications;
        private boolean autocommit;

        private Session() {
            unmodified = new Unmodified();
            modifications = new HashMap<>();
            autocommit = true;
        }

        public Map<K, Row<V>> select(final Set<K> keys, final boolean lock) {
            final Map<K, Row<V>> result = new HashMap<>();
            for (final K key : keys) {
                final Row<V> row;
                try {
                    if (lock) {
                        modifications.compute(key, ($, modification) -> {
                            if (modification == null) {
                                lock(key);
                            }
                            return unmodified;
                        });
                    }
                    row = row(key);
                } finally {
                    if (autocommit && lock) {
                        unlock(key);
                    }
                }
                if (row != null) {
                    result.put(key, row);
                }
            }
            return result;
        }

        public void begin() {
            autocommit = false;
        }

        public void upsert(final K key, final V value, final long modified) {
            execute(key, new Upsert(Row.create(value, modified)));
        }

        public void update(final K key, final V value, final long modified) {
            execute(key, new Update(Row.create(value, modified)));
        }

        public void delete(final K key, final long modified) {
            execute(key, new Delete(Row.create(null, modified)));
        }

        public void commit() {
            end(() -> modifications.forEach((key, modification) -> modification.commit(key)));
        }

        public void rollback() {
            end(() -> {
            });
        }

        private Row<V> row(final K key) {
            final Row<V> row = rows.get(key);
            final Modification modification = modifications.get(key);
            return (modification != null ? modification.apply(row) : row);
        }

        private void execute(final K key, final Modification modification) {
            if (!modifications.containsKey(key)) {
                lock(key);
            }
            if (modification.test(key)) {
                modifications.put(key, modification);
                if (autocommit) {
                    commit();
                }
            }
        }

        private void end(final Runnable task) {
            autocommit = true;
            try {
                task.run();
            } finally {
                modifications.keySet().forEach(InMemoryBucket.this::unlock);
                modifications.clear();
            }
        }

        private abstract class Modification {
            protected abstract boolean test(final K key);

            protected abstract Row<V> apply(Row<V> stored);

            protected abstract void commit(final K key);
        }

        private final class Unmodified extends Modification {
            private Unmodified() {
            }

            @Override
            protected boolean test(final K key) {
                return true;
            }

            @Override
            protected Row<V> apply(final Row<V> stored) {
                return stored;
            }

            @Override
            protected void commit(final K key) {
            }
        }

        private final class Upsert extends Modification {
            private final Row<V> row;

            private Upsert(final Row<V> row) {
                this.row = row;
            }

            @Override
            protected boolean test(final K key) {
                final Row<V> existing = row(key);
                return (existing == null || existing.modified() <= row.modified());
            }

            @Override
            protected Row<V> apply(final Row<V> stored) {
                return row;
            }

            @Override
            protected void commit(final K key) {
                rows.compute(key, ($, existing) -> {
                    if (existing != null && existing.modified() > row.modified()) {
                        throw new InternalError("delete messed up");
                    }
                    return row;
                });
            }
        }

        private final class Update extends Modification {
            private final Row<V> row;

            private Update(final Row<V> row) {
                this.row = row;
            }

            @Override
            protected boolean test(final K key) {
                final Row<V> existing = row(key);
                return (existing != null && existing.modified() <= row.modified());
            }

            @Override
            public Row<V> apply(final Row<V> stored) {
                return row;
            }

            @Override
            public void commit(final K key) {
                rows.compute(key, ($, existing) -> {
                    if (existing == null || existing.modified() > row.modified()) {
                        throw new InternalError("delete messed up");
                    }
                    return row;
                });
            }
        }


        private final class Delete extends Modification {
            private final Row<V> row;

            private Delete(final Row<V> row) {
                this.row = row;
            }

            @Override
            protected boolean test(final K key) {
                final Row<V> existing = row(key);
                return (existing != null && existing.modified() <= row.modified());
            }

            @Override
            public Row<V> apply(final Row<V> stored) {
                return null;
            }

            @Override
            public void commit(final K key) {
                rows.compute(key, ($, existing) -> {
                    if (existing == null || existing.modified() > row.modified()) {
                        throw new InternalError("delete messed up");
                    }
                    return null;
                });
            }
        }
    }

    private static final class Lock {
        private final Object monitor;
        private volatile Thread owner;

        private Lock() {
            monitor = new Object();
        }

        private void lock() {
            final Thread me = Thread.currentThread();
            try {
                synchronized (monitor) {
                    if (owner != me) {
                        while (owner != null) {
                            monitor.wait();
                        }
                        owner = me;
                    }
                }
            } catch (final InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        private void unlock() {
            final Thread me = Thread.currentThread();
            synchronized (monitor) {
                if (owner != me) {
                    throw new IllegalStateException(me + " is not lock owner, " + owner + " is");
                }
                owner = null;
                monitor.notifyAll();
            }
        }
    }
}
