package com.jzallas.lifecycleaware.compiler.generators;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.UiThread;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.lifecycleaware.LifecycleBindingException;
import com.jzallas.lifecycleaware.compiler.Utils;
import com.jzallas.lifecycleaware.compiler.producers.ClassNameProducer;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Creates a Binder for every class that contains {@link LifecycleAware} annotations
 */
public class TargetBinderGenerator extends AbstractClassGenerator {
    private static final Class PARAM_TYPE_LIFECYCLE = Lifecycle.class;
    private static final String PARAM_NAME_LIFECYCLE = "lifecycle";
    private static final String PARAM_NAME_TARGET = "target";

    private List<? extends Element> annotatedElements;
    private Element parent;
    private ClassNameProducer lifecycleObserverProducer;
    private ClassNameProducer wrappedProducer;

    public TargetBinderGenerator(ClassNameProducer producer, Elements elementUtils, Types typeUtils, Messager messager) {
        super(producer, elementUtils, typeUtils, messager);
    }

    public TargetBinderGenerator attachElements(Element parent, List<? extends Element> annotatedElements) {
        this.parent = parent;
        this.annotatedElements = annotatedElements;
        return this;
    }

    public void attachProducers(ClassNameProducer lifecycleObserverProducer, ClassNameProducer wrappedProducer) {

        this.lifecycleObserverProducer = lifecycleObserverProducer;
        this.wrappedProducer = wrappedProducer;
    }

    private CodeBlock buildBindingAll() {
        return annotatedElements.stream()
                .map(this::buildBindingOne)
                .collect(Utils.toCodeBlockBuilder())
                .build();
    }

    private CodeBlock buildBindingOne(Element element) {
        final String lifecycleBindingExceptionType = "uninitializedFailure";

        return CodeBlock.builder()
                .beginControlFlow("if ($L.$L != $L)",
                        PARAM_NAME_TARGET,
                        element.getSimpleName(),
                        null)
                .add(observerStatement(element))
                .nextControlFlow("else")
                .addStatement(
                        "throw $T.$L($L)",
                        LifecycleBindingException.class,
                        lifecycleBindingExceptionType,
                        PARAM_NAME_TARGET
                )
                .endControlFlow()
                .build();
    }

    private CodeBlock observerStatement(Element element) {
        final String lifecycleBindMethod = "addObserver";

        return CodeBlock.builder()
                .addStatement("$L.$L(new $T($L))",
                        PARAM_NAME_LIFECYCLE,
                        lifecycleBindMethod,
                        lifecycleObserverProducer.getClassName(element),
                        wrapObserverIfNecessary(element))
                .build();
    }

    /**
     * Returns the element formatted as a code block if the element is an observer.
     * If the element is not an observer, wrap it in an observer and return that instead.
     *
     * @param element
     * @return
     */
    private CodeBlock wrapObserverIfNecessary(Element element) {
        boolean needsWrapper = !Utils.implementsInterface(elementUtils, typeUtils, element, LifecycleAwareObserver.class);
        if (needsWrapper) {
            return CodeBlock.builder()
                    .add("new $T($L.$L)",
                            wrappedProducer.getClassName(element),
                            PARAM_NAME_TARGET,
                            element.getSimpleName())
                    .build();
        } else {
            return CodeBlock.builder()
                    .add("$L.$L", PARAM_NAME_TARGET, element.getSimpleName())
                    .build();
        }
    }

    @Override
    public MethodSpec defineConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(parent.asType()), PARAM_NAME_TARGET)
                .addParameter(PARAM_TYPE_LIFECYCLE, PARAM_NAME_LIFECYCLE)
                .addCode(buildBindingAll())
                .addAnnotation(UiThread.class)
                .build();
    }

    @Override
    public List<MethodSpec> defineMethods() {
        return ImmutableList.of();
    }

    @Override
    protected String getPackage() {
        return producer.getClassName(parent).packageName();
    }

    @Override
    public TypeSpec defineClass() {
        return TypeSpec.classBuilder(producer.getClassName(parent))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
    }
}
