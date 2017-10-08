package com.jzallas.lifecycleaware.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.compiler.generators.ClassGenerator;
import com.jzallas.lifecycleaware.compiler.generators.LifecycleObserverGenerator;
import com.jzallas.lifecycleaware.compiler.generators.TargetBinderGenerator;
import com.jzallas.lifecycleaware.compiler.validation.ElementChecker;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class LifecycleAwareProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(LifecycleAware.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
    }

    /**
     * Creates a {@link Stream} emitting only {@link Element}s that can be
     * properly handled by this {@link LifecycleAwareProcessor}. This stream will stop the processor
     * if it finds anything that might cause a problem during compile time.
     *
     * @param roundEnvironment environment as provided by {@link #process(Set, RoundEnvironment)}
     * @return filtered {@link Stream} ready to use for processing
     * @throws ProcessingException when the annotation is misused
     */
    private Stream<? extends Element> toElementStream(RoundEnvironment roundEnvironment) {
        final ElementChecker checker = new ElementChecker(elementUtils, typeUtils, messager);
        return roundEnvironment.getElementsAnnotatedWith(LifecycleAware.class)
                .stream()
                .filter(checker::validate);
    }

    /**
     * Attempt to generate the file.
     *
     * @param file configured {@link JavaFile} to write out
     */
    private void writeFile(JavaFile file) {
        try {
            file.writeTo(filer);
        } catch (IOException e) {
            String message =
                    String.format("Failed generating file for %s - %s", file.typeSpec.name, e.getMessage());
            messager.printMessage(Diagnostic.Kind.ERROR, message);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            // Create all the unique lifecycle observers first
            toElementStream(roundEnvironment)
                    .filter(Utils.distinctByKey(element -> element.getAnnotation(LifecycleAware.class).value()))
                    .map(element -> new LifecycleObserverGenerator(elementUtils, messager, typeUtils)
                            .attachElements(element))
                    .map(ClassGenerator::build)
                    .forEach(this::writeFile);

            // Aggregate elements by target
            Map<Element, ? extends List<? extends Element>> targetMapping =
                    toElementStream(roundEnvironment)
                            .collect(Collectors.groupingBy(Element::getEnclosingElement));

            // create all target binders
            targetMapping.keySet()
                    .stream()
                    .map(target -> new TargetBinderGenerator(elementUtils, messager, typeUtils)
                            .attachElements(target, targetMapping.get(target)))
                    .map(ClassGenerator::build)
                    .forEach(this::writeFile);
        } catch (ProcessingException processingException) {
            // if we had a processing exception, then return true to indicate that this
            // processor hasn't completed. The appropriate error should have already been
            // sent via messager by now.
            return true;
        }

        return false;
    }
}
