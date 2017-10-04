package com.jzallas.lifecycleaware.compiler.generators;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Creates a {@link LifecycleObserver} for every requested {@link Lifecycle.Event}
 */
public class LifecycleObserverGenerator extends AbstractClassGenerator {

    private static final String OBSERVER_CLASS_SUFFIX = LifecycleObserver.class.getSimpleName();
    private static final String OBSERVER_PACKAGE_NAME = LifecycleAware.class.getPackage().getName();
    private static final String FIELD_OBSERVER = "observer";
    private static final String ANNOT_DEFAULT_NAME = "value";

    private Element annotatedElement;

    public LifecycleObserverGenerator(Elements elementUtils, Messager messager, Types typeUtils) {
        super(elementUtils, messager, typeUtils);
    }

    public LifecycleObserverGenerator attachElements(Element annotatedElement) {
        this.annotatedElement = annotatedElement;
        return this;
    }

    @Override
    protected String getPackage() {
        return OBSERVER_PACKAGE_NAME;
    }

    private MethodSpec defineLifecycleHook() {

        final String methodName = "onLifecycleEvent";

        Lifecycle.Event lifecycleEvent = annotatedElement.getAnnotation(LifecycleAware.class).value();

        AnnotationSpec archLifeCycleSpec = AnnotationSpec.builder(OnLifecycleEvent.class)
                .addMember(ANNOT_DEFAULT_NAME, "$T.$L", Lifecycle.Event.class, lifecycleEvent)
                .build();

        return MethodSpec.methodBuilder(lifecycleEvent.name())
                .addAnnotation(archLifeCycleSpec)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L.$L($T.$L)", FIELD_OBSERVER, methodName, Lifecycle.Event.class, lifecycleEvent)
                .build();
    }

    static ClassName createObserverName(Element element) {
        LifecycleAware lifecycleCall = element.getAnnotation(LifecycleAware.class);
        String clazz = lifecycleCall.value().name() + OBSERVER_CLASS_SUFFIX;
        return ClassName.get(OBSERVER_PACKAGE_NAME, clazz);
    }

    @Override
    public MethodSpec defineConstructor() {
        final String paramObserver = "observer";
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(LifecycleAwareObserver.class, paramObserver)
                .addStatement("$L.$L = $L", "this", FIELD_OBSERVER, paramObserver)
                .build();
    }

    @Override
    public List<MethodSpec> defineMethods() {
        return ImmutableList.of(
                defineLifecycleHook()
        );
    }

    @Override
    public TypeSpec defineClass() {
        return TypeSpec.classBuilder(createObserverName(annotatedElement))
                .addSuperinterface(LifecycleObserver.class)
                .addField(LifecycleAwareObserver.class, FIELD_OBSERVER, Modifier.PRIVATE)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
    }
}
