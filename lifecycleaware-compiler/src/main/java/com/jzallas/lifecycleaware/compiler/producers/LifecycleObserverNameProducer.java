package com.jzallas.lifecycleaware.compiler.producers;

import android.arch.lifecycle.LifecycleObserver;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

public class LifecycleObserverNameProducer implements ClassNameProducer {

    private static final String OBSERVER_CLASS_SUFFIX = LifecycleObserver.class.getSimpleName();

    private static final String OBSERVER_PACKAGE_NAME = LifecycleAware.class.getPackage().getName();

    @Override
    public ClassName getClassName(Element element) {
        LifecycleAware lifecycleCall = element.getAnnotation(LifecycleAware.class);
        String clazz = join(lifecycleCall.value().name(), OBSERVER_CLASS_SUFFIX);
        return ClassName.get(OBSERVER_PACKAGE_NAME, clazz);
    }
}
