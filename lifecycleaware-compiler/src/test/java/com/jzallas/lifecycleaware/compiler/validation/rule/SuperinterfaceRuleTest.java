package com.jzallas.lifecycleaware.compiler.validation.rule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class SuperinterfaceRuleTest {
    @Mock
    private Elements mockElementUtils;

    @Mock
    private Types mockTypeUtils;

    @Mock
    private Element mockElement;

    @Mock
    private TypeElement mockObserverElement;

    @Mock
    private TypeMirror mockMirror, mockObserverMirror;

    private SuperinterfaceRule testRule;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testRule = new SuperinterfaceRule(mockElementUtils, mockTypeUtils);
        doReturn(mockMirror)
                .when(mockElement)
                .asType();

        doReturn(mockObserverMirror)
                .when(mockObserverElement)
                .asType();
    }

    @Test
    public void testApplyPass() throws Exception {
        doReturn(mockObserverElement)
                .when(mockElementUtils)
                .getTypeElement(anyString());

        doReturn(true)
                .when(mockTypeUtils)
                .isAssignable(mockMirror, mockObserverMirror);

        assertTrue(testRule.apply(mockElement));
    }

    @Test
    public void testApplyFail() throws Exception {
        doReturn(mockObserverElement)
                .when(mockElementUtils)
                .getTypeElement(anyString());

        doReturn(false)
                .when(mockTypeUtils)
                .isAssignable(mockMirror, mockObserverMirror);

        assertFalse(testRule.apply(mockElement));
    }

    @Test
    public void getMessage() throws Exception {
        String message = testRule.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }
}