package com.zutubi.services.mail.core;

import java.io.IOException;
import java.util.Properties;

/**
 * The applications build and version details.
 */
public final class Version {

    private static final Version INSTANCE = new Version();

    private static final String RESOURCE_NAME = "version.properties";

    private static final String BUILD_NUMBER = "build.number";
    private static final String BUILD_TIME = "build.time";
    private static final String VERSION = "version";

    /**
     * The applications build number, assigned by the build server at build time.  This number
     * is unique across all builds.
     */
    private String buildNumber;

    /**
     * The applications build time, assigned by the build server at build time.  This string
     * identifies when the artifact was built.
     */
    private String buildTime;

    /**
     * The applications version number, an indication of the maturity of the software.  This is update
     * with each functional release.
     */
    private String version;

    public static Version getInstance() {
        return INSTANCE;
    }

    private Version() {
        try {
            loadVersionDetails();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadVersionDetails() throws IOException {
        Properties versionProperties = new Properties();
        versionProperties.load(getClass().getResourceAsStream(RESOURCE_NAME));

        buildNumber = versionProperties.getProperty(BUILD_NUMBER);
        buildTime = versionProperties.getProperty(BUILD_TIME);
        version = versionProperties.getProperty(VERSION);
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public String getVersion() {
        return version;
    }

    @Override public String toString() {
        return String.format("%s %s", version, buildNumber);
    }
}
