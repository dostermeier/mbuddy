package com.zutubi.services.mail.core.lifecycle;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

/**
 * The lifecycle component detector is a bean that auto-detects beans within the
 * current application context that implement the {@link Lifecycle} interface.
 */
public class LifecycleDetector implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleDetector.class);

    /**
     * The application context in which this detector is deployed.
     */
    private ApplicationContext applicationContext;

    /**
     * The {@link InitializingBean#afterPropertiesSet()} callback triggers the
     * application context scanning.
     */
    @Override
    public void afterPropertiesSet() {

        for (Lifecycle component : scanForComponents(Lifecycle.class)) {
            // For now, we just log that the component has been detected.
            LOGGER.debug("Detected {}", component.getClass().getName());
        }
    }

    private <T> Collection<T> scanForComponents(Class<T> type) {
        return applicationContext.getBeansOfType(type).values();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
