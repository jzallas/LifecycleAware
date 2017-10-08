package com.jzallas.lifecycleaware.compiler;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;

public class TestUtils {
    /**
     * Asserts that the provided callable fails with the expected error.
     *
     * @param expectedError
     * @param callable
     * @param <T>
     * @throws Exception if an error occurs that was not the expected error
     */
    public static <T> void assertError(Class<?> expectedError, Callable<T> callable) throws Exception {
        Throwable error = null;
        try {
            callable.call();
        } catch (Exception e) {
            if (expectedError.isInstance(e)) {
                error = e;
            } else {
                // rethrow as this is not the error we're looking for
                throw e;
            }
        }

        assertNotNull(error);
    }
}
