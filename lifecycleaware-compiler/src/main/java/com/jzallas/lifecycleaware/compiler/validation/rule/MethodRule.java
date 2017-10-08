package com.jzallas.lifecycleaware.compiler.validation.rule;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.lifecycleaware.compiler.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class MethodRule extends AbstractProcessingRule {

    public MethodRule(Elements elementUtils, Types typeUtils) {
        super(elementUtils, typeUtils);
    }

    @Override
    public boolean apply(Element element) {
        return implementsInterface(element) || hasMethodName(element);

    }

    protected boolean implementsInterface(Element element) {
        return Utils.implementsInterface(elementUtils, typeUtils, element, LifecycleAwareObserver.class);
    }

    private boolean hasMethodName(Element element) {
        String methodName = element.getAnnotation(LifecycleAware.class).method();
        return !methodName.trim().isEmpty();
    }

    @Override
    public String getMessage() {
        String message = "Must implement %s or provide a methodName in order to be eligible for auto binding.";
        return String.format(message, LifecycleAwareObserver.class);
    }
}
