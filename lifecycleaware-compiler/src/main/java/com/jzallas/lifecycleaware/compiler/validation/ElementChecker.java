package com.jzallas.lifecycleaware.compiler.validation;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.compiler.LifecycleAwareProcessor;
import com.jzallas.lifecycleaware.compiler.ProcessingException;
import com.jzallas.lifecycleaware.compiler.validation.rule.MethodRule;
import com.jzallas.lifecycleaware.compiler.validation.rule.ModifierRule;
import com.jzallas.lifecycleaware.compiler.validation.rule.ProcessingRule;
import com.jzallas.lifecycleaware.compiler.validation.rule.TypeRule;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Checks for to see if {@link Element}s can be properly handled by the {@link LifecycleAwareProcessor}
 */
public class ElementChecker {

    private final Elements elementUtils;
    private final Types typeUtils;
    private final Messager messager;

    public ElementChecker(Elements elementUtils, Types typeUtils, Messager messager) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.messager = messager;
    }

    /**
     * Checks for {@link Element}s that can be properly handled by the
     * {@link LifecycleAwareProcessor}. Failed validation is expected to stop the processor.
     *
     * @throws ProcessingException when the annotation is misused
     * @element {@link Element} to validate
     */
    public boolean validate(Element element) {
        getRules().stream()
                .filter(rule -> !rule.apply(element)) // rules that aren't honored
                .map(ProcessingRule::getMessage) // get the rule message
                .forEach(message -> fail(message, element)); // force a failure

        // if we make it this far then the element should be processable
        return true;
    }

    /**
     * Send a message to the provided {@link Messager} before failing loudly
     *
     * @param message
     * @param element
     */
    private void fail(String message, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
        // fail loudly to stop the processing
        throw new ProcessingException();
    }

    /**
     * Get all of the {@link ProcessingRule}s that need to be applied to an {@link Element}
     *
     * @return immutable {@link List} of all rules
     */
    protected List<ProcessingRule> getRules() {
        return ImmutableList.of(
                new ModifierRule(elementUtils, typeUtils),
                new MethodRule(elementUtils, typeUtils),
                new TypeRule(elementUtils, typeUtils)
        );
    }
}
