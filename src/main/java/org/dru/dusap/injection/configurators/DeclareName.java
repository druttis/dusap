package org.dru.dusap.injection.configurators;

public interface DeclareName extends SpecifyScope {
    SpecifyScope named(String name);
}
