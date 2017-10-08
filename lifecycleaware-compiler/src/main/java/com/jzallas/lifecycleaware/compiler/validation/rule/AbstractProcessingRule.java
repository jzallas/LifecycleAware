package com.jzallas.lifecycleaware.compiler.validation.rule;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

abstract class AbstractProcessingRule implements ProcessingRule {

    protected Elements elementUtils;
    protected Types typeUtils;

    protected AbstractProcessingRule(Elements elementUtils, Types typeUtils) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
    }
}
