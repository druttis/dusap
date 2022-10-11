package org.dru.dusap.cache;

import org.dru.dusap.concurrent.Guard;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class FetchingCache<K, V> extends AbstractCache<K, V> {
    private final CacheFetcher<K, V> fetcher;
    private final ReadWriteLock readWriteLock;

    protected FetchingCache(final CacheFetcher<K, V> fetcher) {
        Objects.requireNonNull(fetcher, "fetcher");
        this.fetcher = fetcher;
        readWriteLock = new ReentrantReadWriteLock(true);
    }

    @Override
    public int checksum() {
        return Guard.lock(readLock(), this::checksumInternal);
    }

    @Override
    public final Map<K, V> getAll(final Set<K> keys) {
        return getAllInternal(keys);
    }

    @Override
    public final Map<K, V> peekAll(final Set<K> keys) {
        return Guard.lock(readLock(), () -> peekAllInternal(keys));
    }

    @Override
    public final void putAll(final Map<K, V> map) {
        Guard.lock(writeLock(), () -> putAllInternal(map));
    }

    @Override
    public final void removeAll(final Set<K> keys) {
        Guard.lock(writeLock(), () -> removeAllInternal(keys));
    }

    @Override
    public final void removeAll(final Map<K, V> map) {
        Guard.lock(writeLock(), () -> removeAllInternal(map));
    }

    @Override
    public final void retainAll(final Set<K> keys) {
        Guard.lock(writeLock(), () -> retainAllInternal(keys));
    }

    @Override
    public final void clear() {
        Guard.lock(writeLock(), this::clearInternal);
    }

    protected final Map<K, V> fetchAll(final Set<K> keys) {
        final Map<K, V> result = fetcher.fetchAll(keys);
        putAll(result);
        return result;
    }

    protected final Lock readLock() {
        return readWriteLock.readLock();
    }

    protected final Lock writeLock() {
        return readWriteLock.writeLock();
    }

    protected abstract int checksumInternal();

    protected abstract Map<K, V> getAllInternal(final Set<K> keys);

    protected abstract Map<K, V> peekAllInternal(final Set<K> keys);

    protected abstract void putAllInternal(final Map<K, V> map);

    protected abstract void removeAllInternal(final Set<K> keys);

    protected abstract void removeAllInternal(final Map<K, V> map);

    protected abstract void retainAllInternal(final Set<K> keys);

    protected abstract void clearInternal();
}
