package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Configurator;
import org.dru.dusap.injection.configurators.BindName;
import org.dru.dusap.injection.configurators.CompleteName;
import org.dru.dusap.injection.configurators.DeclareName;
import org.dru.dusap.injection.configurators.ExposeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ConfiguratorImpl implements Configurator, Runnable {
    private final InjectorImpl injector;
    private final List<Runnable> commands;

    ConfiguratorImpl(final InjectorImpl injector) {
        this.injector = Objects.requireNonNull(injector, "injector");
        commands = new ArrayList<>();
    }

    @Override
    public void inherit() {
        injector.inherit(Utils.getStackTrace(2));
    }

    @Override
    public <T> BindName<T> bind(final Class<T> type) {
        return append(new BindImpl<>(injector, type, Utils.getStackTrace(2)));
    }

    @Override
    public <T> CompleteName<T> complete(final Class<T> type) {
        return append(new CompleteImpl<>(injector, type, Utils.getStackTrace(2)));
    }

    @Override
    public DeclareName declare(final Class<?> type) {
        return append(new DeclareImpl<>(injector, type, Utils.getStackTrace(2)));
    }

    @Override
    public ExposeName expose(final Class<?> type) {
        return append(new ExposeImpl<>(injector, type, Utils.getStackTrace(2)));
    }

    @Override
    public void run() {
        commands.forEach(Runnable::run);
    }

    private <T extends Runnable> T append(final T command) {
        commands.add(Objects.requireNonNull(command, "command"));
        return command;
    }
}
