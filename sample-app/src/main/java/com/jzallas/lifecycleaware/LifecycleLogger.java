package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

public class LifecycleLogger extends GenericLogger implements LifecycleAwareObserver {

    @Override
    public void onLifecycleEvent(Lifecycle.Event event) {
        String logMessage = String.format("LifecycleAware event. This event was run during %s", event.name());
        log(logMessage);
    }
}
