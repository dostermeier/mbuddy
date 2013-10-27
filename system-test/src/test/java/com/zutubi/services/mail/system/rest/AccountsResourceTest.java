package com.zutubi.services.mail.system.rest;

import static com.zutubi.services.mail.system.utils.SimpleMailMessageBuilder.simpleMailMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.Test;

import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;

/**
 *
 */
@Test(singleThreaded = true)
public class AccountsResourceTest extends AbstractResourceTest {

    @Test
    public void testGetMessagesByAccount() throws Exception {

        javaMailSender.send(simpleMailMessage()
                .setTo("to@gmail.com")
                .setFrom("from@gmail.com")
                .setSubject("subject")
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
        assertThat(mimeMessage.getSubject(), is("subject"));
    }

    @Test
    public void testGetAccount() {
        MailAPI api = client.getMailAPI();
        assertThat(api.getAccount("1234@gmail.com"), is(nullValue()));

        javaMailSender.send(simpleMailMessage()
                .setTo("1234@gmail.com")
                .setFrom("from@gmail.com")
                .setText("hello world")
                .build()
        );

        assertThat(api.getAccount("1234@gmail.com"), is(notNullValue()));
    }

    @Test
    public void testDeleteAccount() {
        javaMailSender.send(simpleMailMessage()
                .setTo("abc@gmail.com")
                .setFrom("from@gmail.com")
                .setText("hello world")
                .build()
        );

        MailAPI api = client.getMailAPI();
        assertThat(api.getAccount("abc@gmail.com"), is(notNullValue()));

        api.deleteAccount("abc@gmail.com");
        assertThat(api.getAccount("abc@gmail.com"), is(nullValue()));
    }

}
