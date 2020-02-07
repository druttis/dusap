package org.dru.dusap.injection.internal;

import org.dru.dusap.injection.*;
import org.dru.dusap.reflection.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class InjectorImpl implements Injector {
    private static final Logger logger = LoggerFactory.getLogger(Injector.class);

    private final Context context;
    private final InjectorImpl parent;
    private final Class<? extends Module> module;
    private final List<Class<? extends Module>> dependencies;
    private final Map<Key<?>, BindingImpl<?>> bindingByKey;

    InjectorImpl(final Context context, final InjectorImpl parent, final Class<? extends Module> module,
                 final List<Class<? extends Module>> dependencies) {
        this.context = context;
        this.parent = parent;
        this.module = module;
        this.dependencies = dependencies;
        bindingByKey = new ConcurrentHashMap<>();
        bind(Key.of(Injector.class), () -> this, Scopes.NO_SCOPE, "<init>");
    }

    @Override
    public InjectorImpl getParent() {
        return parent;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public InjectorImpl newChild(final Class<? extends Module> module) {
        Objects.requireNonNull(module, "module");
        if (module.isAnnotationPresent(DependsOn.class)) {
            throw new IllegalArgumentException("specified module can not be annotated with @DependsOn: "
                    + module.getName());
        }
        return Context.configureInjector(new InjectorImpl(null, this, module, Collections.emptyList()), module);
    }

    @Override
    public Class<? extends Module> getModule() {
        return module;
    }

    @Override
    public <T> BindingImpl<T> getBinding(final Key<T> key) {
        BindingImpl<T> binding = getBindingOrNull(key);
        if (binding == null) {
            throw new IllegalStateException("not bound: " + key);
        }
        return binding;
    }

    @Override
    public <T> T getInstance(final Key<T> key) {
        return getBinding(key).getInstance();
    }

    @Override
    public <T> T newInstance(final Constructor<? extends T> constructor, final boolean injectMembers) {
        logger.debug("creating new instance using constructor: {}", constructor.toGenericString());
        final T instance = ReflectionUtils.newInstance(constructor, getInstances(constructor));
        if (injectMembers) {
            injectMembers(instance);
        }
        return instance;
    }

    @Override
    public <T> T newInstance(final Class<? extends T> type, final boolean injectMembers) {
        return newInstance(InjectionUtils.getInjectableConstructor(type), injectMembers);
    }

    @Override
    public void injectField(final Object instance, final Field field) {
        logger.debug("injecting field: " + field.toGenericString());
        ReflectionUtils.setField(instance, field, getInstance(Key.of(field)));
    }

    @Override
    public void injectFields(final Object instance) {
        InjectionUtils.getInjectAnnotatedFields(checkInstance(instance).getClass())
                .forEach(field -> injectField(instance, field));
    }

    @Override
    public void injectMethod(final Object instance, final Method method) {
        logger.debug("injecting method: " + method.toGenericString());
        ReflectionUtils.invokeMethod(instance, method, getInstances(method));
    }

    @Override
    public void injectMethods(final Object instance) {
        InjectionUtils.getInjectAnnotatedMethods(checkInstance(instance).getClass())
                .forEach(method -> injectMethod(instance, method));
    }

    @Override
    public void injectMembers(final Object instance) {
        injectFields(checkInstance(instance));
        injectMethods(instance);
    }

    Context getContext() {
        return context;
    }

    List<Class<? extends Module>> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    InjectorImpl getDependency(final Class<? extends Module> module) {
        if (!getDependencies().contains(Objects.requireNonNull(module, "module"))) {
            throw new IllegalArgumentException(getModule() + " is not dependant on " + module);
        }
        return getContext().getInjector(module);
    }

    @SuppressWarnings("unchecked")
    <T> BindingImpl<T> getLocalBinding(final Key<T> key) {
        return (BindingImpl<T>) bindingByKey.get(Objects.requireNonNull(key, "key"));
    }

    <T> BindingImpl<T> getBindingOrNull(final Key<T> key) {
        BindingImpl<T> binding = getLocalBinding(key);
        if (binding != null) {
            return binding;
        } else if (getParent() != null) {
            binding = getParent().getBindingOrNull(key);
            if (binding != null && binding.isExposed()) {
                return binding;
            }
        }
        if (getContext() != null) {
            final List<BindingImpl<T>> bindings = new ArrayList<>();
            for (final Class<? extends Module> dependencies : getDependencies()) {
                binding = getContext().getInjector(dependencies).getLocalBinding(key);
                if (binding != null && binding.isExposed()) {
                    bindings.add(binding);
                }
            }
            if (bindings.size() == 1) {
                return bindings.get(0);
            } else if (bindings.size() > 1) {
                throw new IllegalArgumentException("multiple bindings: " + bindings);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "Convert2MethodRef", "rawtypes"})
    void inherit(final Object source) {
        getDependencies().stream()
                .map(getContext()::getInjector)
                .flatMap(injector -> injector.bindingByKey.values().stream())
                .filter(BindingImpl::isExposed)
                .forEach(binding -> bindingByKey.computeIfAbsent(binding.getKey(), key -> {
                    final BindingImpl<?> neu = new BindingImpl<>(key, source);
                    neu.setSupplier((Supplier) () -> binding.getInstance());
                    neu.setScope(Scopes.NO_SCOPE);
                    neu.expose(source);
                    neu.setCompleter(source);
                    return neu;
                }));
    }

    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    <T> void bind(final Key<T> key, final Supplier<? extends T> supplier, final Scope scope, final Object source) {
        bindingByKey.compute(Objects.requireNonNull(key, "key"), ($, binding) -> {
            if (binding != null) {
                throw new IllegalStateException(source + " - already specified: " + binding);
            }
            binding = new BindingImpl<>(key, source);
            ((BindingImpl<T>) binding).setSupplier(supplier);
            binding.setScope(scope);
            binding.setCompleter(source);
            return binding;
        });
    }

    @SuppressWarnings("unchecked")
    <T> void complete(final Key<T> key, final Supplier<? extends T> supplier, final Object source) {
        final BindingImpl<T> binding = (BindingImpl<T>) bindingByKey.get(key);
        if (binding == null) {
            throw new IllegalStateException("not declared: " + key);
        }
        binding.setSupplier(Objects.requireNonNull(supplier, "supplier"));
        binding.setCompleter(source);
    }

    void declare(final Key<?> key, final Scope scope, final Object source) {
        bindingByKey.compute(Objects.requireNonNull(key, "key"), ($, binding) -> {
            if (binding != null) {
                throw new IllegalStateException(source + " already specified: " + binding);
            }
            binding = new BindingImpl<>(key, source);
            binding.setScope(scope);
            binding.expose(source);
            return binding;
        });
    }

    void expose(final Key<?> key, final Object source) {
        final BindingImpl<?> binding = getLocalBinding(key);
        if (binding == null) {
            throw new IllegalArgumentException("not bound: " + key);
        }
        binding.expose(source);
    }

    private List<Object> getInstances(final Executable executable) {
        return Stream.of(executable.getParameters())
                .map(parameter -> getInstance(Key.of(parameter)))
                .collect(Collectors.toList());
    }

    private Object checkInstance(final Object instance) {
        return Objects.requireNonNull(instance, "instance");
    }

    @Override
    public String toString() {
        if (getModule() != null) {
            return "module " + getModule().getName();
        } else if (getParent() != null) {
            return "child of " + getParent();
        } else {
            return "anonymous injector";
        }
    }
}
