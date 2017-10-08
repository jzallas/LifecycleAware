package com.jzallas.lifecycleaware.compiler.validation.rule;

import com.jzallas.lifecycleaware.LifecycleAware;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeRule extends AbstractProcessingRule {

    public TypeRule(Elements elementUtils, Types typeUtils) {
        super(elementUtils, typeUtils);
    }

    @Override
    public boolean apply(Element element) {
        return ElementKind.FIELD.equals(element.getKind());
    }

    @Override
    public String getMessage() {
        return String.format("Can only mark member/field variables as %s.", LifecycleAware.class);
    }
}
