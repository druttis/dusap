package org.dru.dusap.annotation;

import java.lang.annotation.Annotation;
import java.util.Objects;

public final class Annotations {
    public static void requireAnnotatedWith(final Class<? extends Annotation> annotationType,
                                            final Class<? extends Annotation> annotatedWithType) {
        Objects.requireNonNull(annotatedWithType, "annotatedWithType");
        if (!annotationType.isAnnotationPresent(annotatedWithType))
            throw new IllegalArgumentException(annotationType.getName() + " not annotated with "
                    + annotatedWithType.getName());
    }

    public static void requireAnnotatedWith(final Annotation annotation,
                                            final Class<? extends Annotation> annotatedWithType) {
        requireAnnotatedWith(annotation.annotationType(), annotatedWithType);
    }

    public static <A extends Annotation> AnnotationBuilder<A> annotation(final Class<A> annotationType) {
        return new AnnotationBuilder<>(annotationType);
    }

    private Annotations() {
    }
}
