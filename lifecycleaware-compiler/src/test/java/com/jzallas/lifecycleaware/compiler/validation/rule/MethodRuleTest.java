package com.jzallas.lifecycleaware.compiler.validation.rule;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;

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
import static org.mockito.Mockito.doReturn;

public class MethodRuleTest {
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

    private MethodRule testRule;

    @Mock
    private LifecycleAware mockAnnotation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testRule = new MethodRule(mockElementUtils, mockTypeUtils);
        doReturn(mockMirror)
                .when(mockElement)
                .asType();

        doReturn(mockObserverMirror)
                .when(mockObserverElement)
                .asType();

        doReturn(mockAnnotation)
                .when(mockElement)
                .getAnnotation(LifecycleAware.class);
    }

    @Test
    public void testApplyWithInterfacePass() throws Exception {
        prepareSuperinterfaceUtils(true);

        assertTrue(testRule.apply(mockElement));
    }

    public void testApplyWithMethodPass() throws Exception {
        prepareSuperinterfaceUtils(false);

        doReturn("test_method")
                .when(mockAnnotation)
                .method();

        assertTrue(testRule.apply(mockElement));
    }

    @Test
    public void testApplyFail() throws Exception {
        prepareSuperinterfaceUtils(false);

        doReturn("")
                .when(mockAnnotation)
                .method();

        assertFalse(testRule.apply(mockElement));
    }

    /**
     * Setup the mock util objects to return the provided mock value when checking for the superinterface
     *
     * @param mock <em>true</em> if the mock element 'implements the correct interface'
     */
    private void prepareSuperinterfaceUtils(boolean mock) {
        doReturn(mockObserverElement)
                .when(mockElementUtils)
                .getTypeElement(LifecycleAwareObserver.class.getName());

        doReturn(mock)
                .when(mockTypeUtils)
                .isAssignable(mockMirror, mockObserverMirror);
    }

    @Test
    public void getMessage() throws Exception {
        String message = testRule.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }
}