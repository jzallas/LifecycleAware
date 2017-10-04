package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field that should be auto-attached to a {@link Lifecycle} during lifecycle binding.
 * Note: The field should have {@link LifecycleAwareObserver} implemented
 * or it will be ignored during binding.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface LifecycleAware {
    Lifecycle.Event value();
}