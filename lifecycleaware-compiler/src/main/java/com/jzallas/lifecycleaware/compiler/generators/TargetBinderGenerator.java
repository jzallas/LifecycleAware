package com.jzallas.lifecycleaware.compiler.generators;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.UiThread;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleBindingException;
import com.jzallas.lifecycleaware.compiler.Utils;
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

    private static final String BINDER_CLASS_SUFFIX = LifecycleAware.class.getSimpleName() + "Binder";
    private static final Class PARAM_TYPE_LIFECYCLE = Lifecycle.class;
    private static final String PARAM_NAME_LIFECYCLE = "lifecycle";
    private static final String PARAM_NAME_TARGET = "target";

    private List<? extends Element> annotatedElements;
    private Element parent;

    public TargetBinderGenerator(Elements elementUtils, Messager messager, Types typeUtils) {
        super(elementUtils, messager, typeUtils);
    }

    public TargetBinderGenerator attachElements(Element parent, List<? extends Element> annotatedElements) {
        this.parent = parent;
        this.annotatedElements = annotatedElements;
        return this;
    }

    @Override
    protected String getPackage() {
        return elementUtils.getPackageOf(parent).getQualifiedName().toString();
    }

    private CodeBlock buildBindingAll() {
        return annotatedElements.stream()
                .map(this::buildBindingOne)
                .collect(Utils.toCodeBlockBuilder())
                .build();
    }

    private CodeBlock buildBindingOne(Element element) {
        final String exceptionParamName = "exception";
        final String lifecycleBindingExceptionType = "uninitializedFailure";
        final String lifecycleBindMethod = "addObserver";

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement(
                        "$L.$L(new $T($L.$L))",
                        PARAM_NAME_LIFECYCLE,
                        lifecycleBindMethod,
                        LifecycleObserverGenerator.createObserverName(element),
                        PARAM_NAME_TARGET,
                        element.getSimpleName()
                )
                .nextControlFlow("catch ($T $L)", NullPointerException.class, exceptionParamName)
                .addStatement(
                        "throw $T.$L($L, $L)",
                        LifecycleBindingException.class,
                        lifecycleBindingExceptionType,
                        PARAM_NAME_LIFECYCLE,
                        exceptionParamName
                )
                .endControlFlow()
                .build();
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
    public TypeSpec defineClass() {
        return TypeSpec.classBuilder(parent.getSimpleName() + BINDER_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
    }
}
