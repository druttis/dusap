package org.dru.dusap.inject;

import org.dru.dusap.util.Builder;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.dru.dusap.annotation.Annotations.annotation;

public final class KeyBuilder<T> implements Builder<Key<T>> {
    private final TypeLiteral<T> type;
    private final Map<Class<? extends Annotation>, Annotation> qualifiers;

    KeyBuilder(final TypeLiteral<T> type) {
        this.type = type;
        qualifiers = new ConcurrentHashMap<>();
    }

    public KeyBuilder<T> with(final Annotation qualifier) {
        Objects.requireNonNull(qualifier, "qualifier");
        final Class<? extends Annotation> annotationType = qualifier.annotationType();
        if (!annotationType.isAnnotationPresent(Qualifier.class)) {
            throw new IllegalArgumentException("Annotation not annotated with " + Qualifier.class + ": "
                    + annotationType);
        }
        if (qualifiers.put(annotationType, qualifier) != null) {
            throw new IllegalArgumentException("Annotation already included: " + annotationType);
        }
        return this;
    }

    public KeyBuilder<T> with(final Builder<? extends Annotation> qualifierBuilder) {
        Objects.requireNonNull(qualifierBuilder, "annotationType");
        return with(qualifierBuilder.build());
    }

    public KeyBuilder<T> with(final Class<? extends Annotation> qualifierType) {
        return with(annotation(qualifierType));
    }

    @Override
    public Key<T> build() {
        return new Key<>(type, qualifiers.values());
    }
}
