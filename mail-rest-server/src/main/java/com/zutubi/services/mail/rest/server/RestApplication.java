package com.zutubi.services.mail.rest.server;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.wadl.WadlFeature;

import com.zutubi.services.mail.rest.server.spring.SpringLifecycleListener;
import com.google.common.collect.Maps;

/**
 * Jersey application configuration.
 */
public class RestApplication extends ResourceConfig {

    public RestApplication() {

        // Resource packages.
        packages("com.zutubi.services.mail.rest.server.resources");

        register(JacksonFeature.class);
        register(WadlFeature.class);

        register(SpringLifecycleListener.class);
        property(SpringLifecycleListener.PARENT_CONTEXT_NAME, "restContext");

        registerInstances(new LoggingFilter());
        registerInstances(typeMappingsFilter());
    }

    private UriConnegFilter typeMappingsFilter() {
        Map<String, MediaType> mediaTypeMappings = newHashMap();
        mediaTypeMappings.put("json", MediaType.APPLICATION_JSON_TYPE);
        mediaTypeMappings.put("xml", MediaType.APPLICATION_XML_TYPE);
        return new UriConnegFilter(mediaTypeMappings, Maps.<String, String>newHashMap());
    }

}
