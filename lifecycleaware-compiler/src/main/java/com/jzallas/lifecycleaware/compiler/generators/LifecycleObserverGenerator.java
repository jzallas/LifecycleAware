package com.jzallas.lifecycleaware.compiler.generators;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.lifecycleaware.compiler.producers.ClassNameProducer;
import com.squareup.javapoet.AnnotationSpec;
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
    private static final String FIELD_OBSERVER = "observer";
    private static final String ANNOT_DEFAULT_NAME = "value";

    private Element annotatedElement;

    public LifecycleObserverGenerator(ClassNameProducer producer, Elements elementUtils, Types typeUtils, Messager messager) {
        super(producer, elementUtils, typeUtils, messager);
    }

    public LifecycleObserverGenerator attachElements(Element annotatedElement) {
        this.annotatedElement = annotatedElement;
        return this;
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
        return TypeSpec.classBuilder(producer.getClassName(annotatedElement))
                .addSuperinterface(LifecycleObserver.class)
                .addField(LifecycleAwareObserver.class, FIELD_OBSERVER, Modifier.PRIVATE)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
    }

    @Override
    protected String getPackage() {
        return producer.getClassName(annotatedElement).packageName();
    }

}
