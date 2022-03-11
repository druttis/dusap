package org.dru.dusap.inject;

import org.dru.dusap.inject.node.BindingNode;
import org.dru.dusap.inject.node.ScopeBindingNode;

import java.lang.annotation.Annotation;

public interface NodeVisitor<R, D> {
    <T extends ScopeHandler<A>, A extends Annotation> R visitScopeBindingNode(ScopeBindingNode<A, T> node, D input);

    <T> R visitBindingNode(BindingNode<T> node, D input);
}
