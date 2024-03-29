package org.dru.dusap.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface DependsOn {
    Class<? extends InjectorModule>[] value();
}
