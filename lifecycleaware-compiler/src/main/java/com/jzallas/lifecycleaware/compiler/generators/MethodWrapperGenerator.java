package com.jzallas.lifecycleaware.compiler.generators;

import android.arch.lifecycle.Lifecycle;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
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
 * Creates a wrapper for every element that does not have {@link LifecycleAwareObserver} implemented
 */
public class MethodWrapperGenerator extends AbstractClassGenerator {
    private static final String FIELD_NAME = "wrappedObject";

    private Element annotatedElement;

    public MethodWrapperGenerator(ClassNameProducer producer, Elements elementUtils, Types typeUtils, Messager messager) {
        super(producer, elementUtils, typeUtils, messager);
    }

    public MethodWrapperGenerator attachElements(Element annotatedElement) {
        this.annotatedElement = annotatedElement;
        return this;
    }

    @Override
    public MethodSpec defineConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(annotatedElement.asType()), FIELD_NAME)
                .addStatement("this.$L = $L", FIELD_NAME, FIELD_NAME)
                .build();
    }

    @Override
    public List<MethodSpec> defineMethods() {
        return ImmutableList.of(
                defineWrapperHook()
        );
    }

    private MethodSpec defineWrapperHook() {
        final String methodName = "onLifecycleEvent";
        final String eventParam = "event";

        String annotatedMethod = annotatedElement.getAnnotation(LifecycleAware.class).method();

        return MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addParameter(Lifecycle.Event.class, eventParam)
                .addModifiers(Modifier.PUBLIC)
                .addCode(warningComment())
                .addStatement("this.$L.$L()", FIELD_NAME, annotatedMethod)
                .build();
    }

    private CodeBlock warningComment() {
        // This warning comment exists because it doesn't look like we can use reflection
        // before compilation to determine if the Annotated element actually has
        // the expected method. If the developer runs the annotation processor and the method
        // actually doesn't exist, they will see this comment right above the generated code to
        // hopefully guide them in the correct direction.
        final String elementVarName = Utils.tryToExtractClassName(typeUtils, annotatedElement);
        final String expectedMethod = annotatedElement.getAnnotation(LifecycleAware.class).method();
        return CodeBlock.builder()
                .add("/*\n")
                .add("* This will have a problem compiling if the method $L::$L()\n", elementVarName, expectedMethod)
                .add("* 1. does not exist.\n")
                .add("* 2. is not visible.\n")
                .add("*/\n")
                .build();
    }

    @Override
    public TypeSpec defineClass() {
        return TypeSpec.classBuilder(producer.getClassName(annotatedElement))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(LifecycleAwareObserver.class)
                .addField(TypeName.get(annotatedElement.asType()), FIELD_NAME, Modifier.PRIVATE)
                .build();
    }

    @Override
    protected String getPackage() {
        return producer.getClassName(annotatedElement).packageName();
    }
}
