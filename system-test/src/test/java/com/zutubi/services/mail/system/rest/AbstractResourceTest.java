package com.zutubi.services.mail.system.rest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.zutubi.services.mail.client.MailClient;
import com.zutubi.services.mail.client.MailClientManager;
import com.zutubi.services.mail.system.resources.MailAppServer;
import com.zutubi.services.mail.system.resources.MailAppServerResource;

/**
 *
 */
public abstract class AbstractResourceTest {

    private final MailAppServerResource APP_SERVER = new MailAppServerResource();

    protected MailClient client;
    protected JavaMailSenderImpl javaMailSender;

    @BeforeSuite
    public void beforeSuite() throws Throwable {
        APP_SERVER.before();
    }

    @AfterSuite
    public void afterSuite() throws Throwable {
        APP_SERVER.after();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        MailAppServer appServer = APP_SERVER.getAppServer();
        String connectionUrl = "mail:rest://" + appServer.getHostName() + ":" + appServer.getEnvironment().getRestPort();
        client = MailClientManager.getClient(connectionUrl);

        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("localhost");
        javaMailSender.setPort(appServer.getEnvironment().getSmtpPort());
    }
}
