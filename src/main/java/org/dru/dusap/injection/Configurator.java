package org.dru.dusap.injection;

import org.dru.dusap.injection.configurators.BindName;
import org.dru.dusap.injection.configurators.CompleteName;
import org.dru.dusap.injection.configurators.DeclareName;
import org.dru.dusap.injection.configurators.ExposeName;

public interface Configurator {
    void inherit();

    <T> BindName<T> bind(Class<T> type);

    <T> CompleteName<T> complete(Class<T> type);

    DeclareName declare(Class<?> type);

    ExposeName expose(Class<?> type);
}
