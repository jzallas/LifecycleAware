package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Binds a specific target to a lifecycle.
 *
 * @see #bind(Lifecycle)
 * @see #bind(LifecycleOwner)
 * @see #bind(Object, Lifecycle)
 * @see #bind(Object, LifecycleOwner)
 */
public final class LifecycleBinder extends AbstractBinder {

    private Object target;

    private Lifecycle lifecycle;

    /**
     * Binds all {@link LifecycleAwareObserver}s that are encapsulated by the
     * provided {@link LifecycleOwner} to the owner's {@link Lifecycle} itself.
     *
     * @param owner
     */
    public static void bind(LifecycleOwner owner) {
        bind(owner, owner.getLifecycle());
    }

    /**
     * Binds all {@link LifecycleAwareObserver}s that are encapsulated by the
     * provided {@code target} to the provided {@link LifecycleOwner}'s {@link Lifecycle}.
     *
     * @param target
     * @param owner
     */
    public static void bind(Object target, LifecycleOwner owner) {
        bind(target, owner.getLifecycle());
    }

    /**
     * Binds all {@link LifecycleAwareObserver}s that are encapsulated by the
     * provided {@link Lifecycle} to that same {@link Lifecycle}.
     *
     * @param lifecycle
     */
    public static void bind(Lifecycle lifecycle) {
        bind(lifecycle, lifecycle);
    }

    /**
     * Binds all {@link LifecycleAwareObserver}s that are encapsulated by the
     * provided {@code target} to the provided {@link Lifecycle}.
     *
     * @param target
     * @param lifecycle
     */
    public static void bind(Object target, Lifecycle lifecycle) {
        new LifecycleBinder(target, lifecycle).performBind();
    }

    LifecycleBinder(Object target, Lifecycle lifecycle) {
        this.target = target;
        this.lifecycle = lifecycle;
    }

    private void performBind() {
        performBind(findBindingConstructor());
    }

    void performBind(Constructor<?> bindingConstructor) {
        if (bindingConstructor == null) {
            logWarn("Skipping bind() step because the binding constructor couldn't be found.");
            return;
        }

        //noinspection TryWithIdenticalCatches
        try {
            bindingConstructor.newInstance(target, lifecycle);
        } catch (InstantiationException e) {
            logError("Cannot construct an Abstract Class", e);
            throw LifecycleBindingException.generalFailure(target, e);
        } catch (IllegalAccessException e) {
            logError("Constructor access is not visible.", e);
            throw LifecycleBindingException.generalFailure(target, e);
        } catch (InvocationTargetException e) {
            logError("Constructor threw an unexpected exception", e);
            throw LifecycleBindingException.generalFailure(target, e);
        }
    }

    /**
     * Find the constructor associated with this target.
     * For example: if the target is called "MainActivity",
     * then this looks for the generated "MainActivityLifecycleAwareBinder"
     *
     * @return
     */
    Constructor<?> findBindingConstructor() {
        Class<?> clazz = target.getClass();
        //noinspection TryWithIdenticalCatches
        try {
            String bindingClassName = clazz.getName() + LifecycleAware.class.getSimpleName() + "Binder";
            Class<?> bindingClass = clazz.getClassLoader().loadClass(bindingClassName);
            return bindingClass.getConstructor(clazz, Lifecycle.class);
        } catch (ClassNotFoundException e) {
            logWarn("Couldn't find the generated binding class", e);
        } catch (NoSuchMethodException e) {
            logWarn("Couldn't find the generated binding constructor", e);
        }
        return null;
    }
}
