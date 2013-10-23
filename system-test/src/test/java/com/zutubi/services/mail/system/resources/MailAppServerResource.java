package com.zutubi.services.mail.system.resources;

import static com.google.common.collect.Lists.newArrayList;
import static com.zutubi.services.mail.system.utils.InProject.locateRestWebApp;
import static com.zutubi.services.mail.system.utils.InProject.locateWebApp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zutubi.services.mail.test.testng.rules.ExternalResource;
import com.zutubi.services.mail.test.testng.rules.TemporaryFolder;

/**
 * The Mail Application Server Resource is a reference counting external resource that
 * can be used within tests to manage an instance of the mail application server.
 */
public class MailAppServerResource extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAppServerResource.class);

    private static final List<MailAppServerResource> references = newArrayList();

    private static MailAppServer appServer;
    private static TemporaryFolder tmp;

    @Override
    public void before() throws Throwable {
        synchronized (references) {
            if (references.isEmpty()) {
                LOGGER.info("Starting application server.");

                tmp = new TemporaryFolder();
                tmp.create();

                appServer = new MailAppServer();
                appServer.getEnvironment().setHome(tmp.getRoot());
                appServer.getEnvironment().setResourceBase(locateWebApp());
                appServer.getEnvironment().setRestResourceBase(locateRestWebApp());
                appServer.start();
            }
            references.add(this);
        }
    }

    @Override
    public void after() {
        synchronized (references) {
            references.remove(this);
            if (references.isEmpty()) {
                LOGGER.info("Stopping application server.");
                appServer.stop();

                tmp.delete();
            }
        }
    }

    public MailAppServer getAppServer() {
        return appServer;
    }
}
