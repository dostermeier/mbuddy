package com.zutubi.services.mail.system.rest;

import static com.zutubi.services.mail.system.utils.SimpleMailMessageBuilder.simpleMailMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.client.MailClient;
import com.zutubi.services.mail.client.MailClientManager;
import com.zutubi.services.mail.system.resources.MailAppServer;
import com.zutubi.services.mail.system.resources.MailAppServerResource;

/**
 *
 */
public class MessagesResourceTest {

    private final MailAppServerResource APP_SERVER = new MailAppServerResource();
    private MailClient client;
    private JavaMailSenderImpl javaMailSender;

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

    @Test
    public void testGetMessagesByAccount() throws Exception {

        javaMailSender.send(simpleMailMessage()
                .setTo("to@gmail.com")
                .setFrom("from@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI api = client.getMailAPI();
        List<MailMessage> messages = api.getMessages("to@gmail.com");
        assertThat(messages, hasSize(1));

        MailMessage message = messages.get(0);
        assertThat(message.getEnvelopeReceiver(), is("to@gmail.com"));
        assertThat(message.getEnvelopeSender(), is("from@gmail.com"));

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session, new ByteArrayInputStream(message.getData()));

        assertThat(mimeMessage.isMimeType("text/plain"), is(true));
        assertThat(mimeMessage.getContent().toString(), is("hello world"));
    }

    @Test
    public void testGetMessages() {

        javaMailSender.send(simpleMailMessage()
                .setTo("123@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI mailAPI = client.getMailAPI();
        List<MailMessage> messages = mailAPI.getMessages();

        assertThat(messages.size(), greaterThan(0));
    }

    @Test
    public void testGetMessage() {

        javaMailSender.send(simpleMailMessage()
                .setTo("123@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI mailAPI = client.getMailAPI();
        List<MailMessage> messages = mailAPI.getMessages();

        MailMessage message = Iterables.getFirst(messages, null);

        MailMessage specificMessage = mailAPI.getMessage(message.getId());
        assertThat(specificMessage, is(notNullValue()));
    }
}
