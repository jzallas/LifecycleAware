package com.jzallas.lifecycleaware;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractBinder {
    private static boolean debug = false;

    private static Logger LOG = Logger.getLogger(LifecycleBinder.class.getName());

    /**
     * Enable debug logging
     *
     * @param debug <em>true</em> to enable logging, <em>false</em> to disable
     */
    public static void setDebug(boolean debug) {
        AbstractBinder.debug = debug;
    }

    void logWarn(String message, Throwable throwable) {
        log(Level.WARNING, message, throwable);
    }

    void logWarn(String message) {
        logWarn(message, null);
    }

    void logError(String message, Throwable throwable) {
        log(Level.SEVERE, message, throwable);
    }

    private void log(Level level, String message, Throwable throwable) {
        if (!debug) {
            return;
        }

        if (throwable == null) {
            LOG.log(level, message);
        } else {
            LOG.log(level, message, throwable);
        }
    }
}
