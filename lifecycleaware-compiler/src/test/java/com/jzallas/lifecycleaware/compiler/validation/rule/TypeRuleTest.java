package com.jzallas.lifecycleaware.compiler.validation.rule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class TypeRuleTest {
    @Mock
    private Elements mockElementUtils;

    @Mock
    private Types mockTypeUtils;

    @Mock
    private Element mockElement;

    private TypeRule testRule;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testRule = new TypeRule(mockElementUtils, mockTypeUtils);
    }

    @Test
    public void testApplyPass() throws Exception {
        doReturn(ElementKind.FIELD)
                .when(mockElement)
                .getKind();

        assertTrue(testRule.apply(mockElement));
    }

    @Test
    public void testApplyFail() throws Exception {
        doReturn(ElementKind.METHOD)
                .when(mockElement)
                .getKind();

        assertFalse(testRule.apply(mockElement));
    }

    @Test
    public void getMessage() throws Exception {
        String message = testRule.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

}