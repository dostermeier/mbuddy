package com.zutubi.services.mail.core.lifecycle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * The environment is the central point for the applications configuration.
 * <p/>
 * The following four locations are searched in order when requesting a
 * property.
 * <ul>
 * <li>The JVMs' System properties {@link System#getProperties()}.</li>
 * <li>Any properties that have been pragmatically set during runtime.</li>
 * <li>The applications properties configuration file.</li>
 * <li>The JVMs' System environment {@link System#getenv()}</li>
 * </ul>
 */
public class Environment extends StandardEnvironment implements Serializable {

    private static final long serialVersionUID = 8535489125371241466L;

    public static final String HOME_LOCATION = "home.location";

    public static final String HTTP_PORT = "http.port";
    public static final int DEFAULT_HTTP_PORT = 8400;

    public static final String REST_PORT = "rest.port";
    public static final int DEFAULT_REST_PORT = 8401;

    public static final String SMTP_PORT = "smtp.port";
    public static final int DEFAULT_SMTP_PORT = 8402;

    public static final String HTTP_CONTEXT = "http.context";
    public static final String DEFAULT_HTTP_CONTEXT = "";

    public static final String REST_RESOURCE_BASE = "rest.resource.base";
    public static final String DEFAULT_REST_RESOURCE_BASE = "${home.location}/system/rest";

    public static final String HTTP_RESOURCE_BASE = "http.resource.base";
    public static final String DEFAULT_HTTP_RESOURCE_BASE = "${home.location}/system/www";

    public static final String TMP_LOCATION = "tmp.location";
    public static final String DEFAULT_TMP_LOCATION = "${java.io.tmpdir}";

    public static final String RUNTIME_PROPERTY_SOURCE_NAME = "runtime";
    public static final String DEFAULT_PROPERTY_SOURCE_NAME = "default";
    public static final String EXTERNAL_PROPERTY_SOURCE_NAME = "external";

    /**
     * Input properties resource name.
     */
    private static final String EXTERNAL_PROPERTY_RESOURCE_NAME = "mail.properties";

    /**
     * The property source provider that provides the runtime configured properties.
     */
    private MapPropertySource runtimeSource;
    private MapPropertySource defaultSource;

    @Override
    protected final void customizePropertySources(MutablePropertySources propertySources) {
        try {
            runtimeSource = new MapPropertySource(RUNTIME_PROPERTY_SOURCE_NAME, new HashMap<String, Object>());
            defaultSource = new MapPropertySource(DEFAULT_PROPERTY_SOURCE_NAME, new HashMap<String, Object>());

            MapPropertySource systemSource = new MapPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getSystemProperties());
            ResourcePropertySource propertySource = new ResourcePropertySource(EXTERNAL_PROPERTY_SOURCE_NAME, EXTERNAL_PROPERTY_RESOURCE_NAME);
            SystemEnvironmentPropertySource envSource = new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment());

            // Define the order in which the property sources are accessed.
            propertySources.addLast(systemSource);
            propertySources.addLast(runtimeSource);
            propertySources.addLast(propertySource);
            propertySources.addLast(envSource);
            propertySources.addLast(defaultSource);

            // for properties where we want to allow substitutions we need to add the
            // default values to this source.  Substitutions are only processed in the
            // property value, not in the property value default.
            defaultSource.getSource().put(HTTP_RESOURCE_BASE, DEFAULT_HTTP_RESOURCE_BASE);
            defaultSource.getSource().put(REST_RESOURCE_BASE, DEFAULT_REST_RESOURCE_BASE);
            defaultSource.getSource().put(TMP_LOCATION, DEFAULT_TMP_LOCATION);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set a runtime property value.  These values override configuration values
     * loaded from the properties file and the {@link System#getenv()}, but not from
     * the {@link System#getProperties()}.
     *
     * @param name  the name of the property.
     * @param value the value of the property.
     */
    public final void setRuntimeProperty(String name, Object value) {
        runtimeSource.getSource().put(name, value);
    }

    public File getHome() {
        return getProperty(HOME_LOCATION, File.class);
    }

    public void setHome(File home) {
        setRuntimeProperty(HOME_LOCATION, home.getAbsolutePath());
    }

    public int getHttpPort() {
        return getProperty(HTTP_PORT, Integer.class, DEFAULT_HTTP_PORT);
    }

    public void setHttpPort(int port) {
        setRuntimeProperty(HTTP_PORT, port);
    }

    public int getRestPort() {
        return getProperty(REST_PORT, Integer.class, DEFAULT_REST_PORT);
    }

    public void setRestPort(int port) {
        setRuntimeProperty(REST_PORT, port);
    }

    public int getSmtpPort() {
        return getProperty(SMTP_PORT, Integer.TYPE, DEFAULT_SMTP_PORT);
    }

    public void setSmtpPort(int port) {
        setRuntimeProperty(SMTP_PORT, port);
    }

    public String getContextPath() {
        return getProperty(HTTP_CONTEXT, DEFAULT_HTTP_CONTEXT);
    }

    public void setContextPath(String contextPath) {
        setRuntimeProperty(HTTP_CONTEXT, contextPath);
    }

    public String getResourceBase() {
        return getProperty(HTTP_RESOURCE_BASE);
    }

    public void setResourceBase(String path) {
        setRuntimeProperty(HTTP_RESOURCE_BASE, path);
    }

    public String getRestResourceBase() {
        return getProperty(REST_RESOURCE_BASE);
    }

    public void setRestResourceBase(String path) {
        setRuntimeProperty(REST_RESOURCE_BASE, path);
    }

    public File getTmp() {
        return getProperty(TMP_LOCATION, File.class);
    }

    public void setTmp(File tmp) {
        setRuntimeProperty(TMP_LOCATION, tmp.getAbsolutePath());
    }
}
