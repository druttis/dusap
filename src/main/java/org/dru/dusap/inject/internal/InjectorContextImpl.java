package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.*;
import org.dru.dusap.inject.provider.ProvidesProvider;
import org.dru.dusap.util.ReflectionUtils;

import javax.inject.Scope;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.dru.dusap.inject.InjectionUtils.getAllModules;
import static org.dru.dusap.inject.InjectionUtils.getDependencies;
import static org.dru.dusap.util.ReflectionUtils.getDeclaredMethods;

public final class InjectorContextImpl implements InjectorContext {
    private final Map<Class<? extends Module>, InjectorImpl> injectors;

    public InjectorContextImpl() {
        injectors = new ConcurrentHashMap<>();
    }

    @Override
    public InjectorImpl getInjector(final Class<? extends Module> module) {
        Objects.requireNonNull(module, "module");
        final InjectorImpl injector = injectors.get(module);
        if (injector == null) {
            throw new IllegalArgumentException("No such injector: module=" + module.getName());
        }
        return injector;
    }

    public InjectorImpl createInjector(final Class<? extends Module> module) {
        Objects.requireNonNull(module, "module");
        createInjectors(getAllModules(module));
        return getInjector(module);
    }

    private void createInjectors(final List<Class<? extends Module>> modules) {
        final Map<Class<? extends Module>, Module> instances = new HashMap<>();
        for (final Class<? extends Module> module : modules) {
            final Set<Class<? extends Module>> dependencies = getDependencies(module);
            final InjectorImpl injector = new InjectorImpl(module, dependencies, this);
            injectors.put(module, injector);
            final Module instance = injector.newInstance(module, false);
            instances.put(module, instance);
            dependencies.stream().map(this::getInjector)
                    .forEach(dependency -> dependency.addDependent(module));
        }
        for (final Class<? extends Module> module : modules) {
            final InjectorImpl injector = getInjector(module);
            final Module instance = instances.get(module);
            // Inject fields
            injector.injectFields(instance);
            // Configure
            final BinderImpl binder = new BinderImpl(injector);
            instance.configure(binder);
            binder.bindToInjector();
            // Provides methods
            getDeclaredMethods(module)
                    .filter(method -> method.isAnnotationPresent(Provides.class))
                    .forEach(method -> {
                        final Key<?> key = Key.of(method);
                        injector.bind(new BindingImpl<>(
                                key,
                                method.isAnnotationPresent(Expose.class),
                                new ProvidesProvider<>(instance, method, injector),
                                ReflectionUtils.getAnnotatedAnnotationOrNull(method, Scope.class),
                                injector));
                    });
            // Inject methods
            injector.injectMethods(instance);
        }
    }
}
