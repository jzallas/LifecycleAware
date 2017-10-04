package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

/**
 * Marks a class as a {@link LifecycleAwareObserver}. It does not target a specific lifecycle method.
 * Instead, it relies on components annotated with {@link LifecycleAware} to specify the
 * the lifecycle event to hook into.
 */
public interface LifecycleAwareObserver {
    /**
     * Action to execute during the specified lifecycle event.
     *
     * @param event the {@link Lifecycle.Event} that this observer was registered with
     */
    void onLifecycleEvent(Lifecycle.Event event);
}
