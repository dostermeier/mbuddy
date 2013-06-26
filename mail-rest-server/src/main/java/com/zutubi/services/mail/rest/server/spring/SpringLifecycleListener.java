package com.zutubi.services.mail.rest.server.spring;

import static org.glassfish.hk2.utilities.BuilderHelper.createConstantDescriptor;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.zutubi.services.mail.core.lifecycle.ApplicationContextLocator;

/**
 * JAX-RS Provider class for bootstrapping Jersey 2 Spring integration.
 *
 */
@Provider
public class SpringLifecycleListener implements ContainerLifecycleListener {

    /**
     * The name of the servlet context init parameter that defines the name of the
     * spring context to be retrieved.
     */
    public static final String PARENT_CONTEXT_NAME = "parentContextKey";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLifecycleListener.class);
    private ServiceLocator locator;

    private BeanFactoryReference parentContextRef;

    @Inject
    public SpringLifecycleListener(ServiceLocator loc) {
        LOGGER.debug("SpringLifecycleListener: " + loc);
        locator = loc;
    }

    @Override
    public void onStartup(Container container) {
        LOGGER.debug("onStartup: " + container);

        if (container instanceof ServletContainer) {
            ServletContext servletContext = ((ServletContainer) container).getServletContext();

            String factoryName = servletContext.getInitParameter("parentContextKey");

            this.parentContextRef = ApplicationContextLocator.getInstance().useBeanFactory(factoryName);

            ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) this.parentContextRef.getFactory();

            DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
            DynamicConfiguration dynamicConfiguration = dcs.createDynamicConfiguration();
            dynamicConfiguration.addActiveDescriptor(createConstantDescriptor(new AutowiredInjectResolver(ctx)));
            dynamicConfiguration.addActiveDescriptor(createConstantDescriptor(ctx, null, ApplicationContext.class));
            dynamicConfiguration.commit();

            LOGGER.info("jersey-spring initialized");
        } else {
            LOGGER.info("jersey-spring not initialized");
        }
    }

    @Override
    public void onReload(Container container) {
        LOGGER.debug("onReload: " + container);
    }

    @Override
    public void onShutdown(Container container) {
        LOGGER.debug("onShutdown: " + container);
        if (this.parentContextRef != null) {
            this.parentContextRef.release();
        }
    }

}