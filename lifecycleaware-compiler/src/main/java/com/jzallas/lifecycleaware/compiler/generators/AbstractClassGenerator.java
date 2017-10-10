package com.jzallas.lifecycleaware.compiler.generators;

import com.jzallas.lifecycleaware.compiler.producers.ClassNameProducer;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class AbstractClassGenerator implements ClassGenerator {

    ClassNameProducer producer;
    Elements elementUtils;
    Messager messager;
    Types typeUtils;

    AbstractClassGenerator(ClassNameProducer producer, Elements elementUtils, Types typeUtils, Messager messager){
        this.producer = producer;
        this.elementUtils = elementUtils;
        this.messager = messager;
        this.typeUtils = typeUtils;
    }

    protected abstract String getPackage();

    @Override
    public JavaFile build() {
        TypeSpec classSpec =
                defineClass()
                        .toBuilder()
                        .addMethod(defineConstructor())
                        .addMethods(defineMethods())
                        .build();

        return JavaFile.builder(getPackage(), classSpec)
                .addFileComment("Generated code - Do not modify!")
                .build();
    }
}
