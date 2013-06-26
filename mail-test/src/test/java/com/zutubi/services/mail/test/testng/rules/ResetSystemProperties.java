package com.zutubi.services.mail.test.testng.rules;

import java.util.Properties;

/**
 * An external rule implementation that resets the {@link System#getProperties()}
 * between test executions.
 */
public class ResetSystemProperties extends ExternalResource {

    private Properties snapshot;

    @Override protected void before() throws Throwable {
        snapshot = new Properties();
        for (String key : System.getProperties().stringPropertyNames()) {
            snapshot.setProperty(key, System.getProperty(key));
        }
    }

    @Override protected void after() {
        System.setProperties(snapshot);
    }
}