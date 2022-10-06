package org.dru.dusap.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public final class LockUtils {
    public static <T> T lock(final Lock lock, final Supplier<? extends T> task) {
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    public static void lock(final Lock lock, final Runnable task) {
        lock.lock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    public static boolean tryLock(final Lock lock, final Runnable task) {
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

    public static void tryLock(final Lock lock, final Runnable successor, final Runnable backup) {
        if (!tryLock(lock, successor)) {
            backup.run();
        }
    }

    private LockUtils() {
    }
}
