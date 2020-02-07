package org.dru.dusap.ecs;

import org.dru.dusap.util.Bits;

import java.util.Objects;

public final class Aspect {
    private final Bits all;
    private final Bits one;
    private final Bits none;

    private Aspect(final Bits all, final Bits one, final Bits none) {
        this.all = all;
        this.one = one;
        this.none = none;
    }

    public boolean matches(final Bits bits) {
        if (all != null && !bits.containsAll(all)) {
            return false;
        }
        if (one != null && !bits.intersects(one)) {
            return false;
        }
        return none == null || !bits.intersects(none);
    }

    public boolean matches(final Entity entity) {
        return matches(entity.getComponentBits());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Aspect)) return false;
        final Aspect aspect = (Aspect) o;
        return Objects.equals(all, aspect.all) &&
                Objects.equals(one, aspect.one) &&
                Objects.equals(none, aspect.none);
    }

    @Override
    public int hashCode() {
        return Objects.hash(all, one, none);
    }
}
