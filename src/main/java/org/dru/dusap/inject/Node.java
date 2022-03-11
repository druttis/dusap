package org.dru.dusap.inject;

public interface Node {
    <R, D> R accept(NodeVisitor<R, D> visitor, D input);
}
