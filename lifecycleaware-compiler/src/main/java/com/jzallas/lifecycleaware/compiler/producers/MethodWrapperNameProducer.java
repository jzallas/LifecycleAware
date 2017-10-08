package com.jzallas.lifecycleaware.compiler.producers;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.lifecycleaware.compiler.Utils;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class MethodWrapperNameProducer implements ClassNameProducer {
    private static final String WRAPPER_SUFFIX = LifecycleAwareObserver.class.getSimpleName();

    private Elements elements;
    private Types types;

    public MethodWrapperNameProducer(Elements elements, Types types) {
        this.elements = elements;
        this.types = types;
    }

    @Override
    public ClassName getClassName(Element element) {
        String method = element.getAnnotation(LifecycleAware.class).method();
        String clazz = join(Utils.tryToExtractClassName(types, element), method, WRAPPER_SUFFIX);
        String packageName = elements.getPackageOf(element).getQualifiedName().toString();
        return ClassName.get(packageName, clazz);
    }
}
