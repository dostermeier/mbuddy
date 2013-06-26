package com.zutubi.services.mail.core.lifecycle;

import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import com.zutubi.services.mail.core.Version;

/**
 * The application is the entry point for starting the IO Application. It is responsible for handling
 * the bootstrapping of the spring application context, managing the configuration and all round useful
 * stuff.
 * <p/>
 * The spring bootstrapping occurs through the use of the {@link ApplicationContextLocator}, an extension of
 * the {@link SingletonBeanFactoryLocator}.  This application instance is injected into the bootstrapped
 * spring contexts and so will be available to all components.
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /**
     * The name of the context defined in the core modules META-INF/spring/descriptor.xml
     */
    private static final String CORE_FACTORY_BEAN_NAME = "coreContext";

    // Enable the JUL to SLF4J bridge and do it early.
    static {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }
    }

    /**
     * This flag tracks the running status of the application.
     */
    private boolean running = false;

    /**
     * Environment configuration.
     */
    private Environment environment = new Environment();

    private BeanFactoryReference factoryReference = null;

    private final Lock stateLock = new ReentrantLock();
    private final Condition stateChanged = stateLock.newCondition();

    /**
     * This returns true if the application is running. The application is
     * considered to be running after it has been {@link #start()}ed.
     *
     * @return true if the application is running, false otherwise.
     */
    public boolean isRunning() {
        stateLock.lock();
        try {
            return running;
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * System entry point. This start method should only be triggered once.
     */
    public void start() {

        stateLock.lock();
        try {

            if (running) {
                throw new IllegalStateException("This application instance is already running.");
            }

            LOGGER.info("*** Application: {} ***", Version.getInstance());
            LOGGER.info("*** Application startup commencing ***");

            registerShutdownHook();

            ApplicationContextLocator locator = ApplicationContextLocator.createInstance(this);

            // We need to request the use of one of the available bean factories to trigger the initialisation
            // of the contexts.  Without this call, no spring initialisation would occur.
            factoryReference = locator.useBeanFactory(CORE_FACTORY_BEAN_NAME);

            running = true;

            stateChanged.signalAll();

            LOGGER.info("*** Application startup completed ***");
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected exception starting Application.", e);
            throw e;
        } finally {
            stateLock.unlock();
        }

    }

    /**
     * This method will wait until either the application is stopped.
     *
     * @throws InterruptedException is thrown if this thread is interrupted.
     */
    public void join() throws InterruptedException {
        stateLock.lock();
        try {
            while (running) {
                stateChanged.await();
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * This method will wait until either the timeout is reached or the application
     * is stopped.
     *
     * @param timeout the timeout, in milliseconds, to wait before returning.
     * @throws InterruptedException is thrown if this thread is interrupted.
     */
    public void join(long timeout) throws InterruptedException {

        stateLock.lock();
        try {
            final Date deadline = new Date(System.currentTimeMillis() + timeout);
            while (running) {
                // awaitUntil returns false if the deadline is reached. (not signalled)
                if (!stateChanged.awaitUntil(deadline)) {
                    return;
                }
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Trigger this application to stop.  This will trigger an orderly shutdown and
     * return once completed.
     */
    public void stop() {
        stateLock.lock();
        try {

            if (!running) {
                throw new IllegalStateException("This application instance is not running.");
            }

            LOGGER.info("*** Application shutdown commencing ***");

            BeanFactory factory = factoryReference.getFactory();

            // Trigger the shutdown of all registered spring contexts.
            // The order of the shutdown will be based on the hierarchy
            // of the contexts, leaf contexts before root contexts.
            ContextRegistry registry = factory.getBean(ContextRegistry.class);
            registry.shutdown();

            factoryReference.release();

            ApplicationContextLocator.releaseInstance();

            running = false;

            stateChanged.signalAll();

            LOGGER.info("*** Application shutdown complete ***");
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Get the version of this application build.
     *
     * @return the application version.
     */
    public Version getVersion() {
        return Version.getInstance();
    }

    /**
     * Get the applications environment configurations.
     * <p/>
     * Note that making changes to the environment will not have any impact if
     * the application has already started.
     *
     * @return the application environment.
     */
    public Environment getEnvironment() {
        return environment;
    }

    private void registerShutdownHook() {
        ShutdownHook shutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * The shutdown hook is registered with the jvm to run on shutdown.  This hook
     * triggers shutdown processing for each of the application components.
     */
    private static class ShutdownHook extends Thread {

        private static final String THREAD_NAME = "ShutdownHook";

        private Application application;

        private ShutdownHook(Application application) {
            super(THREAD_NAME);
            this.application = application;
        }

        @Override public void run() {
            if (application.isRunning()) {
                application.stop();
            }
        }
    }
}
