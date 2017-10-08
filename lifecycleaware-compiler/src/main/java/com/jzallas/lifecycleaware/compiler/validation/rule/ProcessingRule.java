package com.jzallas.lifecycleaware.compiler.validation.rule;

import javax.lang.model.element.Element;

public interface ProcessingRule {
    /**
     * Apply a rule to an element.
     *
     * @param element the {@link Element} to check
     * @return <em>true</em> if the rule is honored, <em>false</em> if it isn't
     */
    boolean apply(Element element);

    String getMessage();
}
