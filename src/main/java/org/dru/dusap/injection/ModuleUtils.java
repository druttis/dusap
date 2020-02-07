package org.dru.dusap.injection;

import java.util.*;

public final class ModuleUtils {
    public static List<Class<? extends Module>> getDependencies(final Class<? extends Module> module) {
        final DependsOn dependsOn = module.getAnnotation(DependsOn.class);
        return (dependsOn != null ? Arrays.asList(dependsOn.value()) : Collections.emptyList());
    }

    public static void checkCircularDependency(final Class<? extends Module> module) {
        checkCircularDependency(module, module, new HashSet<>());
    }

    private static void checkCircularDependency(final Class<? extends Module> start,
                                                final Class<? extends Module> current,
                                                final Set<Class<? extends Module>> visited) {
        if (visited.add(current)) {
            getDependencies(current).forEach(dependency -> {
                if (dependency.equals(start)) {
                    throw new IllegalArgumentException(start.getName() + " has circular dependency to itself");
                }
                checkCircularDependency(start, dependency, visited);
            });
        }
    }

    private static int compareModule(final Class<? extends Module> module1, final Class<? extends Module> module2) {
        return depthOf(module1) - depthOf(module2);
    }

    private static int depthOf(final Class<? extends Module> module) {
        return depthOf(module, new HashSet<>(), 0);
    }

    private static int depthOf(final Class<? extends Module> module, final Set<Class<? extends Module>> visited,
                               final int current) {
        int result = current;
        if (visited.add(module)) {
            for (final Class<? extends Module> dependency : getDependencies(module)) {
                result = Math.max(result, depthOf(dependency, visited, current + 1));
            }
        }
        return result;
    }

    private ModuleUtils() {
    }
}
