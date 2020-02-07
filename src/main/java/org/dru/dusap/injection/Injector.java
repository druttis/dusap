package org.dru.dusap.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Injector {
    Injector getParent();

    Injector newChild(final Class<? extends Module> module);

    Class<? extends Module> getModule();

    <T> Binding<T> getBinding(Key<T> key);

    <T> T getInstance(Key<T> key);

    <T> T newInstance(Constructor<? extends T> constructor, boolean injectMembers);

    <T> T newInstance(Class<? extends T> type, boolean injectMembers);

    void injectField(Object instance, Field field);

    void injectFields(Object instance);

    void injectMethod(Object instance, Method method);

    void injectMethods(Object instance);

    void injectMembers(Object instance);
}
