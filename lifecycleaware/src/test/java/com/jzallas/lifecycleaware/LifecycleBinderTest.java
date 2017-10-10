package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class LifecycleBinderTest {

    @Mock
    private Lifecycle mockLifecycle;

    @Mock
    private Constructor mockConstructor;

    private Object testValidTarget, testInvalidTarget;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // can't mock these because we're testing the reflection scenarios
        testValidTarget = new TestTarget();
        testInvalidTarget = new TestTargetWithoutBinder();
    }

    @Test(expected = LifecycleBindingException.class)
    public void testConstructorKnownExceptionFailure() throws Exception {
        InvocationTargetException targetException = mock(InvocationTargetException.class);
        LifecycleBindingException bindingException = mock(LifecycleBindingException.class);

        doReturn(bindingException)
                .when(targetException)
                .getTargetException();
        try {
            validateFailure(targetException);
        } catch (LifecycleBindingException exception) {
            assertSame(bindingException, exception);
            throw exception;
        }
    }

    @Test(expected = LifecycleBindingException.class)
    public void testConstructorUnknownExceptionFailure() throws Exception {
        validateFailure(mock(InvocationTargetException.class));
    }

    @Test(expected = LifecycleBindingException.class)
    public void testConstructorNotVisibleFailure() throws Exception {
        validateFailure(mock(IllegalAccessException.class));
    }

    @Test(expected = LifecycleBindingException.class)
    public void testAbstractClassFailure() throws Exception {
        validateFailure(mock(InstantiationException.class));
    }

    private void validateFailure(Throwable throwable) throws Exception {
        LifecycleBinder binder = new LifecycleBinder(testValidTarget, mockLifecycle);

        Mockito.doThrow(throwable)
                .when(mockConstructor)
                .newInstance(testValidTarget, mockLifecycle);

        binder.performBind(mockConstructor);
    }

    @Test
    public void testConstructorFound() throws Exception {
        LifecycleBinder binder = new LifecycleBinder(testValidTarget, mockLifecycle);

        Assert.assertNotNull(binder.findBindingConstructor());
    }

    @Test
    public void testBindingConstructorNotFound() throws Exception {
        LifecycleBinder binder = new LifecycleBinder(testInvalidTarget, mockLifecycle);

        Assert.assertNull(binder.findBindingConstructor());
    }

    private static class TestTarget {
        // stub
    }

    @SuppressWarnings("unused")
    private static class TestTarget_LifecycleAware_Binder {
        public TestTarget_LifecycleAware_Binder(TestTarget target, Lifecycle lifecycle) {
            // stub
        }
    }

    private static class TestTargetWithoutBinder {
        // stub
    }

}