package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.Module;
import org.dru.dusap.injection.ModuleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class InjectorContext {
    private static final Logger logger = LoggerFactory.getLogger(InjectorContext.class);
    public static InjectorImpl configureInjector(final InjectorImpl injector, final Class<? extends Module> module) {
        final Module instance = injector.newInstance(module, false);
        final ConfiguratorImpl configurator = new ConfiguratorImpl(injector);
        injector.injectFields(instance);
        instance.configure(configurator);
        configurator.run();
        injector.injectMethods(instance);
        return injector;
    }

    private final Map<Class<? extends Module>, InjectorImpl> injectorByModule;

    public InjectorContext() {
        injectorByModule = new ConcurrentHashMap<>();
    }

    public InjectorImpl getInjector(final Class<? extends Module> module) {
        return getInjectorInternal(module);
    }

    private synchronized InjectorImpl getInjectorInternal(final Class<? extends Module> module) {
        ModuleUtils.checkCircularDependency(module);
        InjectorImpl injector = injectorByModule.get(module);
        if (injector == null) {
            final List<Class<? extends Module>> dependencies = ModuleUtils.getDependencies(module);
            traverseModules(dependencies);
            logger.info("configuring {} with dependencies {}", module.getSimpleName(),
                    dependencies.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));
            injector = configureInjector(new InjectorImpl(this, null, module, dependencies), module);
            injectorByModule.put(module, injector);
        }
        return injector;
    }

    private void traverseModules(final Collection<Class<? extends Module>> modules) {
        final Map<Class<? extends Module>, Long> distinctMap = modules.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        distinctMap.values().removeIf(c -> c < 2L);
        if (distinctMap.size() > 0) {
            throw new IllegalArgumentException("duplicate module classes: " + distinctMap);
        }
        modules.forEach(this::getInjectorInternal);
    }
}
