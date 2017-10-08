package com.jzallas.lifecycleaware.compiler.producers;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

public class TargetBinderNameProducer implements ClassNameProducer {
    private static final String BINDER_CLASS_SUFFIX = LifecycleAware.class.getSimpleName() + "Binder";
    private Elements elements;

    public TargetBinderNameProducer(Elements elements) {

        this.elements = elements;
    }

    @Override
    public ClassName getClassName(Element element) {
        String className = join(element.getSimpleName(), BINDER_CLASS_SUFFIX);
        String packageName = elements.getPackageOf(element).getQualifiedName().toString();
        return ClassName.get(packageName, className);
    }
}
