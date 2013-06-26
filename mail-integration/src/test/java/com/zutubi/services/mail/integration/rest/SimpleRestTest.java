package com.zutubi.services.mail.integration.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.client.MailClient;
import com.zutubi.services.mail.client.MailClientManager;
import com.zutubi.services.mail.integration.resources.MailAppServerResource;

/**
 *
 */
public class SimpleRestTest {

    private final MailAppServerResource APP_SERVER = new MailAppServerResource();
    private MailClient client;

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
        String connectionUrl = "mail:rest://localhost:8431";
        client = MailClientManager.getClient(connectionUrl);
    }

    @Test
    public void testSomething() {

        MailAPI api = client.getMailAPI();
        List<MailMessage> messages = api.getMessages("blah");
        assertThat(messages, hasSize(3));
    }

    @Test
    public void testSomethingElse() {

    }
}
