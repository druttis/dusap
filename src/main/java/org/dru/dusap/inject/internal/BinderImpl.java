package org.dru.dusap.inject.internal;

import org.dru.dusap.inject.*;
import org.dru.dusap.inject.node.BindingNode;
import org.dru.dusap.inject.node.ScopeBindingNode;
import org.dru.dusap.util.ReflectionUtils;
import org.dru.dusap.util.TypeLiteral;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BinderImpl implements Binder {
    private final InjectorImpl injector;
    private final List<Node> nodes;

    public BinderImpl(final InjectorImpl injector) {
        Objects.requireNonNull(injector, "injector");
        this.injector = injector;
        nodes = new CopyOnWriteArrayList<>();
        // Install defaults
        bindScope(Singleton.class).toValue(SingletonScopeHandler.INSTANCE);
        bind(Injector.class).toValue(injector);
    }

    @Override
    public <A extends Annotation, T extends ScopeHandler<A>>
    ScopeBindingNode<A, T> bindScope(final Class<A> scopeType) {
        final ScopeBindingNode<A, T> node = new ScopeBindingNode<>(scopeType, injector);
        nodes.add(node);
        return node;
    }

    @Override
    public <T> BindingNode<T> bind(final KeyBuilder<T> keyBuilder) {
        final BindingNode<T> node = new BindingNode<>(keyBuilder, injector);
        nodes.add(node);
        return node;
    }

    @Override
    public <T> BindingNode<T> bind(final Key<T> key) {
        return bind(KeyBuilder.of(key));
    }

    @Override
    public <T> BindingNode<T> bind(final TypeLiteral<T> typeLiteral) {
        return bind(KeyBuilder.of(typeLiteral));
    }

    @Override
    public <T> BindingNode<T> bind(final Class<T> type) {
        return bind(TypeLiteral.of(type));
    }

    public void bindToInjector() {
        final BindToInjectorVisitor visitor = new BindToInjectorVisitor();
        for (final Node node : nodes) {
            node.accept(visitor, injector);
        }
    }

    private static final class BindToInjectorVisitor implements NodeVisitor<Void, InjectorImpl> {
        @Override
        public <T extends ScopeHandler<A>, A extends Annotation>
        Void visitScopeBindingNode(final ScopeBindingNode<A, T> node, final InjectorImpl input) {
            input.bindScope(new ScopeBindingImpl<>(node.getScopeType(), node.getProvider()));
            return null;
        }

        @Override
        public <T> Void visitBindingNode(final BindingNode<T> node, final InjectorImpl input) {
            input.bind(new BindingImpl<>(node.getKey(), node.isExposed(), node.getProvider(), node.getScope(), input));
            return null;
        }
    }
}
