package org.dru.dusap.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

public final class Guard {
    public static <T> T read(final Lock lock, final Supplier<? extends T> task) {
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    public static void write(final Lock lock, final Runnable task) {
        lock.lock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    public static boolean tryWrite(final Lock lock, final Runnable task) {
        if (lock.tryLock()) {
            try {
                task.run();
                return true;
            } finally {
                lock.unlock();
            }
        } else {
            return false;
        }
    }

    private final Lock readLock;
    private final Lock writeLock;

    public Guard(final Lock readLock, final Lock writeLock) {
        this.readLock = readLock;
        this.writeLock = writeLock;
    }

    public Guard(final ReadWriteLock readWriteLock) {
        this(readWriteLock.readLock(), readWriteLock.writeLock());
    }

    public <T> T read(final Supplier<T> method) {
        return read(readLock, method);
    }

    public void write(final Runnable method) {
        write(writeLock, method);
    }

    public boolean tryWrite(final Runnable method) {
        return tryWrite(writeLock, method);
    }

    public <T> T update(final Supplier<T> method) {
        return read(writeLock, method);
    }
}
