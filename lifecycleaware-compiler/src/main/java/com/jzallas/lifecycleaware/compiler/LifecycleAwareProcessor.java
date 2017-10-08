package com.jzallas.lifecycleaware.compiler;

import android.arch.lifecycle.Lifecycle;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.lifecycleaware.compiler.generators.ClassGenerator;
import com.jzallas.lifecycleaware.compiler.generators.LifecycleObserverGenerator;
import com.jzallas.lifecycleaware.compiler.generators.MethodWrapperGenerator;
import com.jzallas.lifecycleaware.compiler.generators.TargetBinderGenerator;
import com.jzallas.lifecycleaware.compiler.producers.LifecycleObserverNameProducer;
import com.jzallas.lifecycleaware.compiler.producers.MethodWrapperNameProducer;
import com.jzallas.lifecycleaware.compiler.producers.TargetBinderNameProducer;
import com.jzallas.lifecycleaware.compiler.validation.ElementChecker;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Checks the annotated {@link Element}s for misuse
     *
     * @param annotatedElements
     */
    private void validate(Collection<? extends Element> annotatedElements) {
        // Check the annotated elements for misuse first
        ElementChecker elementChecker = new ElementChecker(elementUtils, typeUtils, messager);
        annotatedElements.forEach(elementChecker::validate);
    }

    /**
     * Creates all of the required {@link android.arch.lifecycle.LifecycleObserver}s for the provided elements.
     *
     * @param annotatedElements
     */
    private void createLifecycleObservers(Collection<? extends Element> annotatedElements) {
        final LifecycleObserverNameProducer producer = new LifecycleObserverNameProducer();
        final LifecycleObserverGenerator generator =
                new LifecycleObserverGenerator(producer, elementUtils, typeUtils, messager);

        annotatedElements.stream()
                .filter(Utils.distinctByKey(element -> element.getAnnotation(LifecycleAware.class).value()))
                .map(generator::attachElements)
                .map(ClassGenerator::build)
                .forEach(this::writeFile);
    }

    /**
     * Creates all of the required wrappers for observers that don't implement {@link LifecycleAwareObserver}
     *
     * @param annotatedElements
     */
    private void createWrappedObservers(Collection<? extends Element> annotatedElements) {
        final MethodWrapperNameProducer producer = new MethodWrapperNameProducer(elementUtils, typeUtils);
        final MethodWrapperGenerator generator =
                new MethodWrapperGenerator(producer, elementUtils, typeUtils, messager);

        annotatedElements.stream()
                .filter(element -> !Utils.implementsInterface(elementUtils, typeUtils, element, LifecycleAwareObserver.class))
                .filter(Utils.distinctByKey(producer::getClassName))
                .map(generator::attachElements)
                .map(ClassGenerator::build)
                .forEach(this::writeFile);
    }


    /**
     * Creates the binder that attaches the observers to the {@link Lifecycle}.
     *
     * @param annotatedElements
     */
    private void createBinding(Collection<? extends Element> annotatedElements) {
        Map<Element, ? extends List<? extends Element>> targetMapping =
                annotatedElements.stream()
                        .collect(Collectors.groupingBy(Element::getEnclosingElement));

        final TargetBinderNameProducer producer = new TargetBinderNameProducer(elementUtils);
        final TargetBinderGenerator generator =
                new TargetBinderGenerator(producer, elementUtils, typeUtils, messager);

        // binder needs to know the names of the other classes we generated
        generator.attachProducers(
                new LifecycleObserverNameProducer(),
                new MethodWrapperNameProducer(elementUtils, typeUtils)
        );

        // create all target binders
        targetMapping.keySet()
                .stream()
                .map(target -> generator.attachElements(target, targetMapping.get(target)))
                .map(ClassGenerator::build)
                .forEach(this::writeFile);
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            Set<? extends Element> annotatedElements =
                    roundEnvironment.getElementsAnnotatedWith(LifecycleAware.class);

            validate(annotatedElements);

            createLifecycleObservers(annotatedElements);

            createWrappedObservers(annotatedElements);

            createBinding(annotatedElements);

        } catch (ProcessingException processingException) {
            // if we had a processing exception, then return true to indicate that this
            // processor hasn't completed. The appropriate error should have already been
            // sent via messager by now.
            return true;
        }

        return false;
    }
}
