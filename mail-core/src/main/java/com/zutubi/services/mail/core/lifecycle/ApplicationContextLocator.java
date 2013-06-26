package com.zutubi.services.mail.core.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * This class is responsible for bootstrapping the spring application context. It makes extensive
 * use of the existing {@link SingletonBeanFactoryLocator} functionality to scan the classpath
 * for appropriately named spring descriptors, using them to initialise hierarchical application
 * contexts.
 * <p/>
 * The added functionality this class provides is that it injects an instance of the {@link Application}
 * into the root of these contexts so that it is available to all.
 * <p/>
 * To correctly make use of this bootstrapping, each module within the application requires a
 * spring context that defines its context dependencies.  This context file, known as the descriptor,
 * is expected on the classpath as <code>META-INF/spring/descriptor.xml</code>.  This descriptor will
 * define an application context that loads local spring context files and references any dependent
 * contexts through the parent reference.  The name of the application context will be what other
 * descriptors can use to reference it.
 * <p/>
 * For example:
 * <pre>
 *    <bean id="myContext" class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *        <constructor-arg>
 *            <list>
 *                <value>classpath:META-INF/spring/include.xml</value>
 *                <value>classpath:myContext.xml</value>
 *            </list>
 *        </constructor-arg>
 *        <constructor-arg ref="parentContext"/>
 *    </bean>
 * </pre>
 * <p/>
 * This extension of the {@link SingletonBeanFactoryLocator} borrows from the {@link ContextSingletonBeanFactoryLocator}
 * in that it creates an ApplicationContext rather than a BeanFactory from the scanned contexts.
 * <p/>
 * As is the case with the {@link ContextSingletonBeanFactoryLocator} and {@link SingletonBeanFactoryLocator},
 * this object has a set of static factory methods to provide access to the underlying application context.
 * This is required to allow 3rd party integration where control over the instantiation of components is
 * not possible (**cough** ContextLoaderListener **cough**).
 */
public class ApplicationContextLocator extends SingletonBeanFactoryLocator {

    /**
     * Descriptor selector.
     */
    private static final String SELECTOR = "classpath*:META-INF/spring/descriptor*.xml";

    /**
     * The application object which will be injected into the root context of the
     * bootstrapped application contexts.  This object will be available to all
     * contexts through the "application" bean name.
     */
    private Application application;

    /**
     * The locator instance that has access to the spring application contexts.
     */
    private static ApplicationContextLocator INSTANCE;

    /**
     * Create the singleton instance of the locator for the specified application.  This
     * application instance will be placed in a spring context at the root of the
     * scanned contexts allowing beans defined within the context access to the application.
     * The applications environment is also injected into the root context and hence accessible
     * through bean definitions.
     * <p/>
     * Only one ApplicationContextLocator can be instantiated at a time, so repeated calls to this
     * method will result in {@link RuntimeException}s.
     *
     * @param application the application instance that will be used to configure the root context.
     * @return the singleton instance.
     * @throws BeansException on error.
     */
    public static synchronized ApplicationContextLocator createInstance(Application application) throws BeansException {
        if (INSTANCE != null) {
            throw new RuntimeException("Can not create multiple instances.");
        }
        INSTANCE = new ApplicationContextLocator(application);
        return INSTANCE;
    }

    /**
     * Reset the locator singleton.  This should be called if you wish to make a subsequent
     * call {@link #createInstance(Application)} within the same jvm.
     */
    public static synchronized void releaseInstance() {
        INSTANCE = null;
    }

    /**
     * Get the locator instance.  This will only be useful if {@link #createInstance(Application)}
     * has already been called.
     *
     * @return the existing singleton instance, or null if non exists.
     */
    public static ApplicationContextLocator getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new instance of this locator.
     *
     * @param application the application used to configure the root context.
     */
    protected ApplicationContextLocator(Application application) {
        super(SELECTOR);
        this.application = application;
    }

    /**
     * Overrides the default method to create definition object as an ApplicationContext
     * instead of the default BeanFactory. This does not affect what can actually
     * be loaded by that definition.
     * <p/>
     * It also creates a root context for the new ApplicationContext that contains the
     * application instance.
     */
    @Override
    protected BeanFactory createDefinition(String resourceLocation, String factoryKey) {

        // This root context becomes the root context for our application. Anything we
        // add here will be available in all contexts.
        StaticApplicationContext rootContext = new StaticApplicationContext();
        rootContext.setId("rootContext");
        rootContext.setEnvironment(application.getEnvironment());
        rootContext.getBeanFactory().registerSingleton("application", application);
        rootContext.getBeanFactory().registerAlias("application", "app");
        rootContext.refresh();

        // To make our application instance available to the contexts we create, we need
        // to create a context that itself contains a context that can be used as the
        // root context for the contexts we create.  It is a little bit round about, but
        // it works.
        StaticApplicationContext parentContext = new StaticApplicationContext();
        parentContext.getBeanFactory().registerSingleton(rootContext.getId(), rootContext);
        parentContext.refresh();

        return new ClassPathXmlApplicationContext(new String[]{resourceLocation}, false, parentContext);
    }

    /**
     * Overrides the default method to refresh the ApplicationContext, invoking
     * {@link ConfigurableApplicationContext#refresh ConfigurableApplicationContext.refresh()}.
     */
    @Override
    protected void initializeDefinition(BeanFactory groupDef) {
        if (groupDef instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) groupDef).refresh();
        }
    }

    /**
     * Overrides the default method to operate on an ApplicationContext, invoking
     * {@link ConfigurableApplicationContext#refresh ConfigurableApplicationContext.close()}.
     */
    @Override
    protected void destroyDefinition(BeanFactory groupDef, String selector) {
        if (groupDef instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) groupDef).close();
        }
    }
}
