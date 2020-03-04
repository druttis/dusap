package org.dru.dusap.util;

import org.dru.dusap.time.TimeSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPool<T> implements Pool<T> {
    private final TimeSupplier timeSupplier;
    private final int minimumSize;
    private final int maximumSize;
    private final LinkedList<T> available;
    private final Set<T> occupied;
    private final Object monitor;

    public AbstractPool(final TimeSupplier timeSupplier, final int minimumSize, final int maximumSize) {
        this.timeSupplier = Objects.requireNonNull(timeSupplier, "timeSupplier");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("negative minimumSize: " + minimumSize);
        }
        if (maximumSize < minimumSize) {
            throw new IllegalArgumentException("maximumSize is less than minimumSize: minimumSize="
                    + minimumSize + ", maximumSize=" + maximumSize);
        }
        if (maximumSize == 0) {
            throw new IllegalArgumentException("maximumSize has to be 1 or grater: " + maximumSize);
        }
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        available = new LinkedList<>();
        occupied = new HashSet<>();
        monitor = new Object();
    }

    protected final void finish() {
        while (size() < minimumSize) {
            introduce();
        }
    }

    @Override
    public final T acquire(final Duration duration) throws InterruptedException {
        final Instant now = timeSupplier.get();
        final Instant timeouts = (duration != null ? now.plus(duration) : Instant.MAX);
        T item;
        synchronized (monitor) {
            while (true) {
                item = await(Duration.between(timeSupplier.get(), timeouts));
                if (isValid(item)) {
                    break;
                } else {
                    destroy(item);
                }
            }
            occupied.add(item);
        }
        return item;
    }

    @Override
    public final T acquire() throws InterruptedException {
        return acquire(null);
    }

    @Override
    public final void release(final T item) {
        Objects.requireNonNull(item, "item");
        synchronized (monitor) {
            if (!occupied.remove(item)) {
                throw new IllegalArgumentException("unknown item: " + item);
            }
            if (isValid(item)) {
                available.addLast(item);
            } else if (size() < minimumSize) {
                introduce();
            }
        }
    }

    @Override
    public final int size() {
        return available.size() + occupied.size();
    }

    private T await(final Duration timeout) throws InterruptedException {
        while (available.isEmpty()) {
            if (size() < maximumSize) {
                available.addLast(create());
                break;
            }
            monitor.wait(TimeUnit.SECONDS.toMillis(timeout.getSeconds()), timeout.getNano());
        }
        return available.removeFirst();
    }

    private void introduce() {
        available.addLast(create());
    }

    protected abstract T create();

    protected abstract boolean isValid(final T item);

    protected abstract void destroy(final T item);
}