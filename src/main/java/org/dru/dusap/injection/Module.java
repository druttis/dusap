package org.dru.dusap.injection;

import org.dru.dusap.injection.configurators.BindName;
import org.dru.dusap.injection.configurators.CompleteName;
import org.dru.dusap.injection.configurators.DeclareName;
import org.dru.dusap.injection.configurators.ExposeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Module {
    private final Logger logger;
    private final AtomicReference<Configurator> configuratorRef;

    protected Module() {
        logger = LoggerFactory.getLogger(getClass());
        configuratorRef = new AtomicReference<>();
    }

    public final void configure(final Configurator configurator) {
        if (!configuratorRef.compareAndSet(null, Objects.requireNonNull(configurator, "configurator"))) {
            throw new IllegalStateException("configurator already set");
        }
        logger.debug("configuring injector");
        configure();
    }

    protected final void inherit() {
        getConfigurator().inherit();
    }

    protected final <T> BindName<T> bind(final Class<T> type) {
        return getConfigurator().bind(type);
    }

    protected final <T> CompleteName<T> complete(final Class<T> type) {
        return getConfigurator().complete(type);
    }

    protected final DeclareName declare(final Class<?> type) {
        return getConfigurator().declare(type);
    }

    protected final ExposeName expose(final Class<?> type) {
        return getConfigurator().expose(type);
    }

    private Configurator getConfigurator() {
        return Objects.requireNonNull(configuratorRef.get(), "configurator not set");
    }

    protected abstract void configure();
}
