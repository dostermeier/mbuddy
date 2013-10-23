package com.zutubi.services.mail.rest.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.yammer.metrics.jetty.InstrumentedHandler;
import com.yammer.metrics.jetty.InstrumentedQueuedThreadPool;
import com.yammer.metrics.jetty.InstrumentedSelectChannelConnector;
import com.zutubi.services.mail.core.lifecycle.Environment;
import com.zutubi.services.mail.core.lifecycle.LifecycleComponent;

import ch.qos.logback.access.jetty.RequestLogImpl;

/**
 * The RestLifecycleComponent is responsible for managing the embedded jetty server
 * that runs the REST API.
 */
public class RestLifecycleComponent extends LifecycleComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestLifecycleComponent.class);
    private static final String ACCESS_LOG_CONFIGURATION_RESOURCE = "/logback-rest-access.xml";

    /**
     * Indicates whether or not this component is currently running.
     */
    private boolean running;

    /**
     * The environment that defines the configuration of the rest api.
     */
    private Environment environment;

    /**
     * The embedded jetty server instance that is managed by this lifecycle component.
     */
    private Server server;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        Assert.notNull(environment);

        server = new Server();
        server.setConnectors(new Connector[]{new InstrumentedSelectChannelConnector(environment.getRestPort())});
        server.setThreadPool(new InstrumentedQueuedThreadPool());
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {

        LOGGER.info("*** RestLifecycleComponent startup commencing ***");

        HandlerCollection requestHandlers = new HandlerCollection();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(environment.getContextPath());
        webapp.setResourceBase(environment.getRestResourceBase());
        requestHandlers.addHandler(new InstrumentedHandler(webapp));

        RequestLogImpl requestLog = new RequestLogImpl();
        requestLog.setResource(ACCESS_LOG_CONFIGURATION_RESOURCE);

        RequestLogHandler logHandler = new RequestLogHandler();
        logHandler.setRequestLog(requestLog);
        requestHandlers.addHandler(logHandler);

        try {
            server.setHandler(requestHandlers);
            server.start();
            LOGGER.info("    Jetty server listening on {}", environment.getRestPort());

            running = true;
            LOGGER.info("*** RestLifecycleComponent startup completed ***");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        LOGGER.info("*** RestLifecycleComponent stop commencing ***");

        try {
            if (server.isRunning()) {
                server.stop();
            }
            running = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("*** RestLifecycleComponent stop completed ***");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 10;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
