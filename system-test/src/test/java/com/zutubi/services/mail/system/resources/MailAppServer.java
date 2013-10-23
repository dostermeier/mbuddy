package com.zutubi.services.mail.system.resources;

import com.zutubi.services.mail.core.lifecycle.Application;
import com.zutubi.services.mail.core.lifecycle.Environment;

/**
 * The Application Server is a wrapper around the Mail {@link Application}
 * that represents an active deployment of application.  As such, it provides
 * some important information about the server, such as how it can be accessed.
 */
public class MailAppServer {

    private Application application;

    public MailAppServer() {
        application = new Application();
    }

    /**
     * Retrieve the host on which the io application is available.
     *
     * @return the host string.
     */
    public String getHostName() {
        return "localhost";
    }

    public String whereIs(String relativeUrl) {
        return getHttpApi().whereIs(relativeUrl);
    }

    private String getCommonPath(String relativeUrl) {
        if (!relativeUrl.startsWith("/")) {
            relativeUrl = getEnvironment().getContextPath() + relativeUrl;
        }
        return relativeUrl;
    }

    /**
     * Start the io application.
     *
     * @see Application#start()
     */
    public void start() {
        application.start();
    }

    /**
     * Stop the io application.
     *
     * @see Application#stop()
     */
    public void stop() {
        application.stop();
    }

    public void deployOn(String path) {
        getEnvironment().setContextPath(path);
    }

    public Application getApplication() {
        return application;
    }

    public Environment getEnvironment() {
        return application.getEnvironment();
    }

    /**
     * Get the API instance for the RESTful API.
     *
     * @return the RESTful API instance.
     */
    public API getRestApi() {
        return new API() {
            @Override
            public String whereIs(String resource) {
                String path = getCommonPath(resource);
                return "http://" + getHostName() + ":" + getEnvironment().getRestPort() + path;
            }
        };
    }

    /**
     * Get the API instance for the Http API.
     *
     * @return the HTTP API instance.
     */
    public API getHttpApi() {
        return new API() {
            @Override
            public String whereIs(String resource) {
                String path = getCommonPath(resource);
                return "http://" + getHostName() + ":" + getEnvironment().getHttpPort() + path;
            }
        };
    }

    /**
     * The App Server provides a number of APIs.  This interface provides
     * an abstraction for API specific requests.
     */
    public interface API {

        /**
         * Retrieve the absolute url of the resource relative to the
         * current API.
         *
         * @param resource the relative resource whose location we are requesting.
         * @return the absolute url.
         */
        String whereIs(String resource);
    }
}
