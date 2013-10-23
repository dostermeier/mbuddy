package com.zutubi.services.mail.system.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.client.MailClient;
import com.zutubi.services.mail.client.MailClientManager;
import com.zutubi.services.mail.core.lifecycle.Environment;
import com.zutubi.services.mail.core.utils.Clock;
import com.zutubi.services.mail.core.utils.SystemClock;
import com.zutubi.services.mail.system.resources.MailAppServer;
import com.zutubi.services.mail.system.resources.MailAppServerResource;

/**
 *
 */
public class MessagesResourceTest {

    private Clock clock = new SystemClock();

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
        MailAppServer appServer = APP_SERVER.getAppServer();
        String connectionUrl = "mail:rest://" + appServer.getHostName() + ":" + appServer.getEnvironment().getRestPort();
        client = MailClientManager.getClient(connectionUrl);
    }

    @Test
    public void testGetMessages() throws Exception {

        sendEmail("to@gmail.com", "hello world");

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

    private void sendEmail(String recipient, String message) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Environment environment = APP_SERVER.getAppServer().getEnvironment();

        // Get a Properties object
        Properties props = new Properties();
        props.setProperty("mail.smtps.host", "localhost");
        props.setProperty("mail.smtp.port", String.valueOf(environment.getSmtpPort()));

/*
        props.setProperty("mail.smtps.auth", "true");
        props.setProperty("mail.smtps.quitwait", "false");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
*/


        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress("from@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

        msg.setSubject("test");
        msg.setText(message, "utf-8");
        msg.setSentDate(clock.now());

        Transport.send(msg);

/*
        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
*/
    }
}
