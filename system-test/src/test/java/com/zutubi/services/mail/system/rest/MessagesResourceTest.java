package com.zutubi.services.mail.system.rest;

import static com.zutubi.services.mail.system.utils.SimpleMailMessageBuilder.simpleMailMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

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
public class MessagesResourceTest extends AbstractResourceTest {

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
        assertThat(message, is(notNullValue()));

        MailMessage specificMessage = mailAPI.getMessage(message.getId());
        assertThat(specificMessage, is(notNullValue()));
    }

    @Test
    public void testDeleteMessages() {
        javaMailSender.send(simpleMailMessage()
                .setTo("123@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI mailAPI = client.getMailAPI();
        assertThat(mailAPI.getMessages().size(), greaterThan(0));

        mailAPI.deleteMessages();
        assertThat(mailAPI.getMessages().size(), is(0));
    }

    @Test
    public void testDeleteMessage() {
        javaMailSender.send(simpleMailMessage()
                .setTo("123@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI mailAPI = client.getMailAPI();
        MailMessage message = Iterables.getFirst(mailAPI.getMessages(), null);
        assertThat(message, is(notNullValue()));

        mailAPI.deleteMessage(message.getId());
        message = mailAPI.getMessage(message.getId());
        assertThat(message, is(nullValue()));
    }
}
