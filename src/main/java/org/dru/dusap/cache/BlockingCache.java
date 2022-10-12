package org.dru.dusap.cache;

import org.dru.dusap.concurrent.Guard;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public abstract class BlockingCache<K, V> implements Cache<K, V> {
    private final Guard guard;

    public BlockingCache() {
        guard = new Guard(new ReentrantReadWriteLock(true));
    }

    @Override
    public final Map<K, V> peekAll() {
        return read(this::peekAllImpl);
    }

    @Override
    public final Map<K, V> peekAll(final Set<K> keys) {
        return read(() -> peekAllImpl(keys));
    }

    @Override
    public final void putAll(final Map<K, V> map) {
        write(() -> putAllImpl(map));
    }

    @Override
    public void updateAll(final Map<K, CacheUpdate<V>> map) {
        write(() -> updateAllImpl(map));
    }

    @Override
    public void removeAll(final Set<K> keys) {
        write(() -> removeAllImpl(keys));
    }

    @Override
    public void removeAll(final Map<K, V> map) {
        write(() -> removeAllImpl(map));
    }

    @Override
    public final void retainAll(final Set<K> keys) {
        write(() -> retainAllImpl(keys));
    }

    @Override
    public void clear() {
        write(this::clearImpl);
    }

    protected final <T> T read(final Supplier<T> method) {
        return guard.read(method);
    }

    protected final void write(final Runnable method) {
        guard.write(method);
    }

    protected final boolean tryWrite(final Runnable method) {
        return guard.tryWrite(method);
    }

    protected abstract Map<K, V> peekAllImpl();

    protected abstract Map<K, V> peekAllImpl(Set<K> keys);

    protected abstract void putAllImpl(Map<K, V> map);

    protected abstract void updateAllImpl(Map<K, CacheUpdate<V>> map);

    protected abstract void removeAllImpl(Set<K> keys);

    protected abstract void removeAllImpl(Map<K, V> map);

    protected abstract void retainAllImpl(Set<K> keys);

    protected abstract void clearImpl();
}
