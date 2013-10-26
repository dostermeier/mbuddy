package com.zutubi.services.mail.rest.server.spring;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 * Provide injection support for accessing the spring context via the {@link Autowired} annotation.
 *
 * Note: This implementation does NOT support {@link Qualifier} or {@link Value}.
 */
@Singleton
public class AutowiredInjectResolver implements InjectionResolver<Autowired> {

    // TODO: Investigate spring support for the {@link Inject} annotation and how it all interacts

    private static final Logger LOGGER = LoggerFactory.getLogger(AutowiredInjectResolver.class);
    private ApplicationContext ctx;

    public AutowiredInjectResolver(ApplicationContext ctx) {
        LOGGER.debug("AutowiredInjectResolver()");
        this.ctx = ctx;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        LOGGER.debug("resolve: " + injectee);
        Type t = injectee.getRequiredType();
        if(t instanceof Class) {
            return ctx.getBean((Class<?>)t);
        } else {
            LOGGER.warn("unable to resolve, injectee type not class");
        }
        return null;
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return true;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return true;
    }

}