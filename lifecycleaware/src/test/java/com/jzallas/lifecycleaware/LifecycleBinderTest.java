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
    public void testConstructorExceptionFailure() throws Exception {
        validateFailure(Mockito.mock(InvocationTargetException.class));
    }

    @Test(expected = LifecycleBindingException.class)
    public void testConstructorNotVisibleFailure() throws Exception {
        validateFailure(Mockito.mock(IllegalAccessException.class));
    }

    @Test(expected = LifecycleBindingException.class)
    public void testAbstractClassFailure() throws Exception {
        validateFailure(Mockito.mock(InstantiationException.class));
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
    private static class TestTargetLifecycleAwareBinder {
        public TestTargetLifecycleAwareBinder(TestTarget target, Lifecycle lifecycle) {
            // stub
        }
    }

    private static class TestTargetWithoutBinder {
        // stub
    }

}