package com.jzallas.lifecycleaware;

/**
 * Problems that occur during the lifecycle binding process
 */
public final class LifecycleBindingException extends RuntimeException {

    public static LifecycleBindingException generalFailure(Object target, Throwable throwable) {
        return new LifecycleBindingException(bindingFailureMessage(target), throwable);
    }

    public static LifecycleBindingException uninitializedFailure(Object target) {
        final String reminderTemplate = "Did you remember to initialize the observer before calling %s.bind()?";
        final String initFailMessage = String.format(reminderTemplate, LifecycleBinder.class.getSimpleName());
        return new LifecycleBindingException(bindingFailureMessage(target) + " " + initFailMessage);
    }

    private static String bindingFailureMessage(Object target) {
        return String.format("Failed to bind to lifecycle for %s.", target.getClass().getSimpleName());
    }

    private LifecycleBindingException(String string, Throwable throwable) {
        super(string, throwable);
    }

    private LifecycleBindingException(String string) {
        super(string);
    }
}
