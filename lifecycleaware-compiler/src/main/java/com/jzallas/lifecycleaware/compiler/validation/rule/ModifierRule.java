package com.jzallas.lifecycleaware.compiler.validation.rule;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ModifierRule extends AbstractProcessingRule {

    public ModifierRule(Elements elementUtils, Types typeUtils) {
        super(elementUtils, typeUtils);
    }

    @Override
    public boolean apply(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        boolean invalidModifiers = modifiers.contains(Modifier.PRIVATE)
                || modifiers.contains(Modifier.PROTECTED)
                || modifiers.contains(Modifier.FINAL);
        return !invalidModifiers;
    }

    @Override
    public String getMessage() {
        return "Variable must have either public or package level visibility.";
    }
}
