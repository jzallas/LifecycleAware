package com.jzallas.lifecycleaware.compiler;

import com.squareup.javapoet.CodeBlock;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public class Utils {
    /**
     * Convenience method to help filtering distinct items in a {@link java.util.stream.Stream} by the provided {@link Predicate}
     *
     * @param keyExtractor function that generates a key from {@link T}
     * @param <T>
     * @return {@link Predicate} to be used with {@link java.util.stream.Stream#filter(Predicate)}
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * {@link Collector} used combine a {@link java.util.stream.Stream} of {@link CodeBlock}s into a single {@link CodeBlock}.
     *
     * @return
     */
    public static Collector<CodeBlock, CodeBlock.Builder, CodeBlock.Builder> toCodeBlockBuilder() {
        return Collector.of(
                CodeBlock::builder,
                CodeBlock.Builder::add,
                (builder, builder2) -> builder.add(builder2.build())
        );
    }
}
