package com.jzallas.lifecycleaware.compiler.validation.rule;

import com.jzallas.lifecycleaware.LifecycleAwareObserver;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class SuperinterfaceRule extends AbstractProcessingRule {

    public SuperinterfaceRule(Elements elementUtils, Types typeUtils) {
        super(elementUtils, typeUtils);
    }

    @Override
    public boolean apply(Element element) {
        String className = LifecycleAwareObserver.class.getName();
        TypeMirror classType = elementUtils.getTypeElement(className).asType();
        return typeUtils.isAssignable(element.asType(), classType);
    }

    @Override
    public String getMessage() {
        return String.format("Must implement %s in order to be eligible for auto binding.", LifecycleAwareObserver.class);
    }
}
