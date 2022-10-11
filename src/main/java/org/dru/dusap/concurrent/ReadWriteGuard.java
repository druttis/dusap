package org.dru.dusap.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class ReadWriteGuard {
    private final ReadWriteLock readWriteLock;

    public ReadWriteGuard() {
        readWriteLock = new ReentrantReadWriteLock(true);
    }

    public <T> T reading(final Supplier<T> method) {
        return Guard.lock(readLock(), method);
    }

    public void writing(final Runnable method) {
        Guard.lock(writeLock(), method);
    }

    public <T> T writing(final Supplier<T> method) {
        return Guard.lock(writeLock(), method);
    }

    public boolean tryWrite(final Runnable method) {
        return Guard.tryLock(writeLock(), method);
    }

    private Lock readLock() {
        return readWriteLock.readLock();
    }

    private Lock writeLock() {
        return readWriteLock.writeLock();
    }
}
