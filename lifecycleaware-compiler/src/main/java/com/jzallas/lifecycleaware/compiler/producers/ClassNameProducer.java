package com.jzallas.lifecycleaware.compiler.producers;

import com.google.common.base.Joiner;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

public interface ClassNameProducer {
    ClassName getClassName(Element element);

    default String join(Object... objects) {
        // underscores are typically not a standard coding style in Java
        // so if we use underscores here, the chance of naming collisions is much lower
        return Joiner.on("_").join(objects);
    }
}
