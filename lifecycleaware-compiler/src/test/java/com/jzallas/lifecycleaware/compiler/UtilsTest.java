package com.jzallas.lifecycleaware.compiler;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.doReturn;

public class UtilsTest {

    private static class TestPair {
        String first;
        String second;

        TestPair(String first, String second) {
            this.first = first;
            this.second = second;
        }
    }

    @Test
    public void testDistinctByKey() throws Exception {
        List<TestPair> testPairList =
                ImmutableList.of(
                        new TestPair("a", "1"),
                        new TestPair("a", "2"),
                        new TestPair("a", "3"),
                        new TestPair("b", "1")
                );

        List<TestPair> distinctByFirst = testPairList.stream()
                .filter(Utils.distinctByKey(testPair -> testPair.first))
                .collect(Collectors.toList());

        Assert.assertNotEquals(testPairList, distinctByFirst);
        Assert.assertEquals(2, distinctByFirst.size());
    }

    @Test
    public void testToCodeBlock() throws Exception {
        CodeBlock expected = CodeBlock.builder()
                .addStatement("//one")
                .addStatement("//two")
                .addStatement("//three")
                .build();

        List<CodeBlock> codeblocks = ImmutableList.of(
                CodeBlock.builder().addStatement("//one").build(),
                CodeBlock.builder().addStatement("//two").build(),
                CodeBlock.builder().addStatement("//three").build()
        );

        CodeBlock actual = codeblocks.stream()
                .collect(Utils.toCodeBlockBuilder())
                .build();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testImplementsInterface() throws Exception {
        Elements mockElementUtils = Mockito.mock(Elements.class);
        Types mockTypeUtils = Mockito.mock(Types.class);

        TypeElement mockElement = Mockito.mock(TypeElement.class);
        doReturn(mockElement)
                .when(mockElementUtils)
                .getTypeElement(TestInterface.class.getName());

        TypeMirror mockMirror = Mockito.mock(TypeMirror.class);
        doReturn(mockMirror)
                .when(mockElement)
                .asType();

        doReturn(true)
                .when(mockTypeUtils)
                .isAssignable(mockMirror, mockMirror);

        boolean actual = Utils.implementsInterface(mockElementUtils, mockTypeUtils, mockElement, TestInterface.class);

        Assert.assertTrue(actual);
    }

    private interface TestInterface {
    }

}