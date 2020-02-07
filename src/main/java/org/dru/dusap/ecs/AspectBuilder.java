package org.dru.dusap.ecs;

import org.dru.dusap.util.Bits;

public final class AspectBuilder {
    private final Engine engine;
    private Bits all;
    private Bits one;
    private Bits none;

    AspectBuilder(final Engine engine) {
        this.engine = engine;
    }

    public AspectBuilder all(final Class<?>... types) {
        all = engine.bits(types);
        return this;
    }

    public AspectBuilder one(final Class<?>... types) {
        one = engine.bits(types);
        return this;
    }

    public AspectBuilder none(final Class<?>... types) {
        none = engine.bits(types);
        return this;
    }
}
