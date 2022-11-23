package org.dru.dusap.inject;

import org.dru.dusap.util.Builder;
import org.dru.dusap.util.TypeLiteral;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public interface Injector {
    Class<? extends InjectorModule> getModule();

    Set<Class<? extends InjectorModule>> getDependencies();

    Injector getDependency(Class<? extends InjectorModule> module);

    Set<Class<? extends InjectorModule>> getDependents();

    InjectorContext getContext();

    Map<Class<? extends Annotation>, ScopeBinding<?>> getLocalScopeBindings();

    Map<Class<? extends Annotation>, ScopeBinding<?>> getScopeBindings();

    <S extends Annotation> ScopeHandler<S> getScopeHandler(S scope);

    <T> T getInstance(InjectorQuery<T> query);

    <T> T getInstance(Key<T> key);

    <T> T getInstance(Builder<Key<T>> key);

    <T> T getInstance(TypeLiteral<T> typeLiteral);

    <T> T getInstance(Class<T> type);

    <T> T newInstance(Constructor<T> constructor, boolean injectMembers);

    <T> T newInstance(Constructor<T> constructor);

    <T> T newInstance(TypeLiteral<T> typeLiteral, boolean injectMembers);

    <T> T newInstance(TypeLiteral<T> typeLiteral);

    <T> T newInstance(Class<T> type, boolean injectMembers);

    <T> T newInstance(Class<T> type);

    void injectField(Object instance, Field field);

    void injectFields(Object instance);

    Object injectMethod(Object instance, Method method);

    void injectMethods(Object instance);

    void injectMembers(Object instance);
}
