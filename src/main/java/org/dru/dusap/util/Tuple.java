package org.dru.dusap.util;

import java.util.Objects;

public final class Tuple<L, R> {
    private L left;
    private R right;

    public Tuple(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public Tuple() {
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        final Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(getLeft(), tuple.getLeft()) &&
                Objects.equals(getRight(), tuple.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeft(), getRight());
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
