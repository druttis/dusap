package org.dru.dusap.inject;

import org.dru.dusap.reflection.Reflections;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public final class Injections {
    public static <T> Constructor<T> getInjectableConstructor(Class<T> type) {
        final List<Constructor<T>> constructors
                = Reflections.getDeclaredConstructors(type).collect(Collectors.toList());
        if (constructors.isEmpty()) {
            throw new InjectionException("No constructor found: " + type.getName());
        }
        if (constructors.size() == 1) {
            return constructors.get(0);
        }
        constructors.removeIf(constructor -> !constructor.isAnnotationPresent(Inject.class));
        if (constructors.isEmpty()) {
            throw new InjectionException("Several constructors of which none is @Inject annotated: " + type.getName());
        }
        if (constructors.size() == 1) {
            return constructors.get(0);
        }
        throw new InjectionException("Several constructors of which more than one is @Inject annotated: "
                + type.getName());
    }

    public static Set<Class<? extends InjectorModule>> getDependencies(final Class<? extends InjectorModule> module) {
        Objects.requireNonNull(module, "module");
        final Set<Class<? extends InjectorModule>> dependencies = new HashSet<>();
        final DependsOn dependsOn = module.getAnnotation(DependsOn.class);
        if (dependsOn != null) {
            for (final Class<? extends InjectorModule> dependency : dependsOn.value()) {
                if (!dependencies.add(dependency)) {
                    throw new IllegalArgumentException("Duplicate dependency '" + dependency.getName()
                            + "': module=" + module.getName());
                }
            }
        }
        return dependencies;
    }

    public static List<Class<? extends InjectorModule>> getAllModules(final Class<? extends InjectorModule> module) {
        final Set<Class<? extends InjectorModule>> visited = new HashSet<>();
        getAllModules(module, visited);
        final List<Class<? extends InjectorModule>> dependencyModules = new ArrayList<>(visited);
        dependencyModules.sort(Injections::compareModuleTopology);
        return dependencyModules;
    }

    public static void checkNoCircularity(final Class<? extends InjectorModule> module) {
        checkNoCircularity(module, new HashSet<>(), module);
    }

    private static void getAllModules(final Class<? extends InjectorModule> module,
                                      final Set<Class<? extends InjectorModule>> visited) {
        if (visited.add(module)) {
            checkNoCircularity(module);
            getDependencies(module)
                    .forEach(dependencyModule -> getAllModules(dependencyModule, visited));
        }
    }

    private static int compareModuleTopology(final Class<? extends InjectorModule> m1, final Class<? extends InjectorModule> m2) {
        final int t1 = getModuleTopology(m1);
        final int t2 = getModuleTopology(m2);
        return Integer.compare(t1, t2);
    }

    private static int getModuleTopology(final Class<? extends InjectorModule> module) {
        return getModuleTopology(module, new HashSet<>(), 0);
    }

    private static int getModuleTopology(final Class<? extends InjectorModule> module,
                                         final Set<Class<? extends InjectorModule>> visited, final int current) {
        int result = current;
        if (visited.add(module)) {
            for (final Class<? extends InjectorModule> dependencyModule : getDependencies(module)) {
                result = Math.max(result, getModuleTopology(dependencyModule, visited, current + 1));
            }
        }
        return result;
    }

    private static void checkNoCircularity(final Class<? extends InjectorModule> module, final Set<Object> visited,
                                           final Class<? extends InjectorModule> bang) {
        if (visited.add(module)) {
            final Set<Class<? extends InjectorModule>> dependencyModules = getDependencies(module);
            if (dependencyModules.contains(bang)) {
                throw new IllegalArgumentException("Circular dependency graph: " + bang);
            }
            dependencyModules.forEach(dependencyModule -> checkNoCircularity(dependencyModule, visited, bang));
        }
    }

    private Injections() {
    }
}
