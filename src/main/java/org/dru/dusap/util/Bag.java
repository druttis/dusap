package org.dru.dusap.util;

import java.util.Arrays;
import java.util.Objects;

public class Bag<T> {
    private T[] items;
    private int size;

    @SuppressWarnings("unchecked")
    public Bag() {
        items = (T[]) new Object[3];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public T get(final int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return items[index];
    }

    public int indexOf(final T item) {
        for (int index = 0; index < size; index++) {
            if (items[index].equals(item)) {
                return index;
            }
        }
        return -1;
    }

    public boolean contains(final T item) {
        return indexOf(item) != -1;
    }

    public void add(final T item) {
        Objects.requireNonNull(item, "item");
        if (size >= items.length) {
            items = Arrays.copyOf(items, size * 3 / 2 + 1);
        }
        items[size++] = item;
    }

    public T remove(final int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final T item = items[index];
        items[index] = items[--size];
        items[size] = null;
        return item;
    }

    public boolean remove(final T item) {
        final int index = indexOf(item);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    public T removeLast() {
        return remove(size() - 1);
    }

    public T set(final int index, final T item) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Objects.requireNonNull(item, "item2");
        final T old = items[index];
        items[index] = item;
        return old;
    }

    public void clear() {
        for (int index = 0; index < size; index++) {
            items[index] = null;
        }
    }
}
