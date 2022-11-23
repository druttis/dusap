package org.dru.dusap.function;

import java.util.Objects;
import java.util.function.*;

public final class Throwing {
    @SuppressWarnings("unchecked")
    public static <T, E extends Throwable> T raise(final Throwable throwable) throws E {
        throw (E) throwable;
    }

    public static <T, U> BiConsumer<T, U> wrap(final ThrowingBiConsumer<T, U, ?> throwingBiConsumer) {
        Objects.requireNonNull(throwingBiConsumer, "throwingBiConsumer");
        return (t, u) -> {
            try {
                throwingBiConsumer.accept(t, u);
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }

    public static <T, U, R> BiFunction<T, U, R> wrap(final ThrowingBiFunction<T, U, R, ?> throwingBiFunction) {
        Objects.requireNonNull(throwingBiFunction, "throwingBiFunction");
        return (t, u) -> {
            try {
                return throwingBiFunction.apply(t, u);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }

    public static <T, U> BiPredicate<T, U> wrap(final ThrowingBiPredicate<T, U, ?> throwingBiPredicate) {
        Objects.requireNonNull(throwingBiPredicate, "throwingBiPredicate");
        return (t, u) -> {
            try {
                return throwingBiPredicate.test(t, u);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }

    public static <T> Consumer<T> wrap(final ThrowingConsumer<T, ?> throwingConsumer) {
        Objects.requireNonNull(throwingConsumer, "throwingConsumer");
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }

    public static <T, R> Function<T, R> wrap(final ThrowingFunction<T, R, ?> throwingFunction) {
        Objects.requireNonNull(throwingFunction, "throwingFunction");
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }

    public static <T> Predicate<T> wrap(final ThrowingPredicate<T, ?> throwingPredicate) {
        return t -> {
            try {
                return throwingPredicate.test(t);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }

    public static Runnable wrap(final ThrowingRunnable<?> throwingRunnable) {
        Objects.requireNonNull(throwingRunnable, "throwingRunnable");
        return () -> {
            try {
                throwingRunnable.run();
            } catch (final Throwable throwable) {
                raise(throwable);
            }
        };
    }

    public static <T> Supplier<T> wrap(final ThrowingSupplier<T, ?> throwingSupplier) {
        Objects.requireNonNull(throwingSupplier, "throwingSupplier");
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }

    private Throwing() {
    }
}
