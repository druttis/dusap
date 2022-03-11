package org.dru.dusap.inject;

import org.dru.dusap.util.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.dru.dusap.inject.Keys.key;

public final class Key<T> {
    private final TypeLiteral<T> typeLiteral;
    private final Set<? extends Annotation> qualifiers;

    Key(final TypeLiteral<T> typeLiteral, final Collection<? extends Annotation> qualifiers) {
        Objects.requireNonNull(typeLiteral, "typeLiteral");
        Objects.requireNonNull(qualifiers, "qualifiers");
        this.typeLiteral = typeLiteral;
        this.qualifiers = Collections.unmodifiableSet(new HashSet<>(qualifiers));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Key<?>)) return false;
        final Key<?> key = (Key<?>) o;
        return typeLiteral.equals(key.typeLiteral) && qualifiers.equals(key.qualifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeLiteral, qualifiers);
    }

    @Override
    public String toString() {
        return "Key{" +
                "typeLiteral=" + typeLiteral +
                ", qualifiers=" + qualifiers +
                '}';
    }

    public TypeLiteral<T> typeLiteral() {
        return typeLiteral;
    }

    public Set<? extends Annotation> qualifiers() {
        return qualifiers;
    }

    public KeyBuilder<T> builder() {
        final KeyBuilder<T> keyBuilder = key(typeLiteral());
        qualifiers().forEach(keyBuilder::with);
        return keyBuilder;
    }
}
