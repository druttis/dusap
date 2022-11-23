package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.*;
import org.dru.dusap.annotation.Annotations;
import org.dru.dusap.inject.InjectorModule;
import org.dru.dusap.util.Builder;
import org.dru.dusap.reflection.Reflections;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.dru.dusap.inject.Injections.getInjectableConstructor;
import static org.dru.dusap.inject.Keys.key;
import static org.dru.dusap.reflection.Reflections.*;

public class InjectorImpl implements Injector {
    private static final ScopeHandler<Annotation> NO_SCOPE_HANDLER = new ScopeHandler<Annotation>() {
        @Override
        public <T> Provider<? extends T> scope(final Annotation scope, final Key<T> key,
                                               final Provider<? extends T> unscoped) {
            return unscoped;
        }
    };

    private final Class<? extends InjectorModule> module;
    private final Set<Class<? extends InjectorModule>> dependencies;
    private final Set<Class<? extends InjectorModule>> dependents;
    private final InjectorContextImpl injectorContext;
    private final Map<Class<? extends Annotation>, ScopeBinding<?>> localScopeBindings;
    private final Map<Key<?>, Binding<?>> localBindings;

    public InjectorImpl(final Class<? extends InjectorModule> module, final Set<Class<? extends InjectorModule>> dependencies,
                        final InjectorContextImpl injectorContext) {
        Objects.requireNonNull(module, "module");
        Objects.requireNonNull(injectorContext, "injection");
        this.module = module;
        this.dependencies = unmodifiableSet(dependencies);
        this.injectorContext = injectorContext;
        dependents = ConcurrentHashMap.newKeySet();
        localScopeBindings = new ConcurrentHashMap<>();
        localBindings = new ConcurrentHashMap<>();
    }

    @Override
    public Class<? extends InjectorModule> getModule() {
        return module;
    }

    @Override
    public Set<Class<? extends InjectorModule>> getDependencies() {
        return dependencies;
    }

    @Override
    public InjectorImpl getDependency(final Class<? extends InjectorModule> module) {
        Objects.requireNonNull(module, "module");
        if (!dependencies.contains(module)) {
            throw new IllegalArgumentException("Not a dependency: " + module.getName());
        }
        return getContext().getInjector(module);
    }

    @Override
    public Set<Class<? extends InjectorModule>> getDependents() {
        return Collections.unmodifiableSet(dependents);
    }

    @Override
    public InjectorContextImpl getContext() {
        return injectorContext;
    }

    @Override
    public Map<Class<? extends Annotation>, ScopeBinding<?>> getLocalScopeBindings() {
        return unmodifiableMap(localScopeBindings);
    }

    @Override
    public Map<Class<? extends Annotation>, ScopeBinding<?>> getScopeBindings() {
        final Map<Class<? extends Annotation>, ScopeBinding<?>> scopeBindings
                = new HashMap<>(getLocalScopeBindings());
        getDependencies().forEach(dependency ->
                getContext().getInjector(dependency).getScopeBindings().forEach(scopeBindings::putIfAbsent));
        return scopeBindings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends Annotation> ScopeHandler<S> getScopeHandler(final S scope) {
        if (scope == null) {
            return (ScopeHandler<S>) NO_SCOPE_HANDLER;
        }
        Annotations.requireAnnotatedWith(scope, Scope.class);
        return (ScopeHandler<S>) getScopeBindings().get(scope.annotationType()).getScopeHandler();
    }

    @Override
    public <T> T getInstance(final InjectorQuery<T> query) {
        Objects.requireNonNull(query, "query");
        final Key<T> key = query.getKey();
        final List<BindingImpl<T>> bindings = new ArrayList<>();
        if (query.getTarget() == null) {
            final BindingImpl<T> binding = getLocalBindingOrNull(key);
            addToBindingsIfNotNull(binding, bindings);
            if (binding == null) {
                getDependencies().stream()
                        .map(dependency -> getContext().getInjector(dependency))
                        .forEach(injector -> addToBindingsIfExposed(injector.getLocalBindingOrNull(key), bindings));
                getDependents().stream()
                        .map(dependent -> getContext().getInjector(dependent))
                        .forEach(injector -> addToBindingsIfNotNull(injector.getLocalBindingOrNull(key), bindings));
            }
        } else if (query.getTarget() == module) {
            addToBindingsIfNotNull(getLocalBindingOrNull(key), bindings);
        } else {
            final InjectorImpl injector = getDependency(query.getTarget());
            addToBindingsIfExposed(injector.getLocalBindingOrNull(key), bindings);
        }
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("No such binding: key=" + key);
            // JIT IT?!
        } else if (bindings.size() > 1) {
            throw new IllegalArgumentException("Multiple bindings: key=" + key);
        } else {
            return bindings.get(0).getInstance();
        }
    }

    @Override
    public <T> T getInstance(final Key<T> key) {
        return getInstance(InjectorQuery.of(key, getModule()));
    }

    @Override
    public <T> T getInstance(final Builder<Key<T>> key) {
        return getInstance(key.build());
    }

    @Override
    public <T> T getInstance(final TypeLiteral<T> typeLiteral) {
        return getInstance(key(typeLiteral));
    }

    @Override
    public <T> T getInstance(final Class<T> type) {
        return getInstance(TypeLiteral.of(type));
    }

    @Override
    public <T> T newInstance(final Constructor<T> constructor, final boolean injectMembers) {
        final T instance = Reflections
                .newInstance(constructor, getParameters(constructor).map(InjectorQuery::of).map(this::getInstance));
        if (injectMembers) {
            injectMethods(instance);
        }
        return instance;
    }

    @Override
    public <T> T newInstance(final Constructor<T> constructor) {
        return newInstance(constructor, true);
    }

    @Override
    public <T> T newInstance(final TypeLiteral<T> typeLiteral, final boolean injectMembers) {
        return newInstance(getInjectableConstructor(typeLiteral.getRawType()), injectMembers);
    }

    @Override
    public <T> T newInstance(final TypeLiteral<T> typeLiteral) {
        return newInstance(typeLiteral, true);
    }

    @Override
    public <T> T newInstance(final Class<T> type, final boolean injectMembers) {
        return newInstance(TypeLiteral.of(type), injectMembers);
    }

    @Override
    public <T> T newInstance(final Class<T> type) {
        return newInstance(type, true);
    }

    @Override
    public void injectField(final Object instance, final Field field) {
        setFieldValue(instance, field, InjectorQuery.of(field));
    }

    @Override
    public void injectFields(final Object instance) {
        Objects.requireNonNull(instance, "instance");
        getDeclaredFields(instance.getClass())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> injectField(instance, field));
    }

    @Override
    public Object injectMethod(final Object instance, final Method method) {
        return invokeMethod(instance, method, getParameters(method).map(InjectorQuery::of).map(this::getInstance));
    }

    @Override
    public void injectMethods(final Object instance) {
        Objects.requireNonNull(instance, "instance");
        getDeclaredMethods(instance.getClass())
                .filter(method -> method.isAnnotationPresent(Inject.class))
                .forEach(method -> injectMethod(instance, method));
    }

    @Override
    public void injectMembers(final Object instance) {
        injectFields(instance);
        injectMethods(instance);
    }

    public void addDependent(final Class<? extends InjectorModule> dependent) {
        Objects.requireNonNull(dependent, "dependent");
        if (!dependents.add(dependent)) {
            throw new IllegalStateException("Dependent already exist: " + dependent.getName());
        }
    }

    public <S extends Annotation> void bindScope(final ScopeBinding<S> scopeBinding) {
        Objects.requireNonNull(scopeBinding, "scopeBinding");
        if (localScopeBindings.putIfAbsent(scopeBinding.annotationType(), scopeBinding) != null) {
            throw new IllegalStateException("Scope binding already exist: " + scopeBinding.annotationType());
        }
    }

    public <T> void bind(final Binding<T> binding) {
        Objects.requireNonNull(binding, "binding");
        if (localBindings.putIfAbsent(binding.getKey(), binding) != null) {
            throw new IllegalStateException("Binding already exist: " + binding.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> BindingImpl<T> getLocalBindingOrNull(final Key<T> key) {
        return (BindingImpl<T>) localBindings.get(key);
    }

    private <T> void addToBindingsIfNotNull(final BindingImpl<T> binding, final List<BindingImpl<T>> bindings) {
        if (binding != null) {
            bindings.add(binding);
        }
    }

    private <T> void addToBindingsIfExposed(final BindingImpl<T> binding, final List<BindingImpl<T>> bindings) {
        if (binding != null && binding.isExposed()) {
            bindings.add(binding);
        }
    }
}
