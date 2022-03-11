package org.dru.dusap.inject;

import javax.inject.Named;

import static org.dru.dusap.annotation.Annotations.annotation;

public final class Names {
    public static Named named(final String name) {
        return annotation(Named.class).with(name).build();
    }

    private Names() {
    }
}
