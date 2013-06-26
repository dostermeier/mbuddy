package com.zutubi.services.mail.core.lifecycle;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

/**
 * The LifecycleComponent is an implementation of the {@link SmartLifecycle} extended to
 * run within the context of the application.
 *
 * It contains utility code for the registration of each bean that extends this class to
 * ensure that the shutdown lifecycle calls are triggered on application shutdown.  It also
 * provides a default implementation for the {@link SmartLifecycle#stop(Runnable)} method
 * that delegates to {@link SmartLifecycle#stop()} and handles the callback.
 */
public abstract class LifecycleComponent implements ApplicationContextAware, InitializingBean, SmartLifecycle {

    /**
     * The application context in which this bean exists.
     */
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * After all the properties are set, we register this beans context with the
     * systems {@link ContextRegistry} so that on shutdown, this context can be closed.
     *
     * By closing the context, we can ensure that the lifecycle is stopped.
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(context);

        ContextRegistry contextRegistry = context.getBean(ContextRegistry.class);
        Assert.notNull(contextRegistry);

        contextRegistry.register(context);
    }

    /**
     * Base implementation of the {@link SmartLifecycle#stop(Runnable)} method that
     * delegates to the {@link SmartLifecycle#stop()} method and then triggers the
     * callback.
     *
     * @param callback  the callback needs to be triggered when stopping is complete.
     *
     * @see SmartLifecycle#stop(Runnable)
     */
    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }
}