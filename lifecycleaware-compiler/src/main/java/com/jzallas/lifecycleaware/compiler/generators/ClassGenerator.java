package com.jzallas.lifecycleaware.compiler.generators;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

public interface ClassGenerator {
    MethodSpec defineConstructor();

    List<MethodSpec> defineMethods();

    TypeSpec defineClass();

    JavaFile build();
}
