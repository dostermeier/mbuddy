package com.zutubi.services.mail.core.server;

import static com.google.common.collect.Lists.newArrayList;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.auth.LoginAuthenticationHandler;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.PlainAuthenticationHandler;
import org.subethamail.smtp.auth.PluginAuthenticationHandler;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.MessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.core.lifecycle.Environment;

/**
 *
 */
@Component
public class MailServer implements MailAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServer.class);

    @Autowired
    private Environment environment;

    /** */
    private SMTPServer server;

    /** */
    private Map<String, List<MailMessage>> messages = Collections.synchronizedMap(Maps.<String, List<MailMessage>>newHashMap());


    @PostConstruct
    public void init() {

        LOGGER.info("*** Starting mail server on port {} ***", environment.getSmtpPort());

        List<MessageListener> listeners = newArrayList();
        listeners.add(new MessageReceiver());

        this.server = new SMTPServer(listeners);
        this.server.setPort(environment.getSmtpPort());
        ((MessageListenerAdapter) server.getMessageHandlerFactory())
                .setAuthenticationHandlerFactory(new AuthHandlerFactory());

        this.server.start();
    }

    @PreDestroy
    public void destroy() {
        this.server.stop();
    }

    @Override
    public List<MailMessage> getMessages(String account) {
        List<MailMessage> accountMessages = messages.get(account);
        if (accountMessages != null) {
            return accountMessages;
        }
        return newArrayList();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     *
     */
    private class AuthHandlerFactory implements AuthenticationHandlerFactory {
        public AuthenticationHandler create() {
            PluginAuthenticationHandler ret = new PluginAuthenticationHandler();
            UsernamePasswordValidator validator = new UsernamePasswordValidator() {
                public void login(String username, String password)
                        throws LoginFailedException {
                    LOGGER.info("Username=" + username);
                    LOGGER.info("Password=" + password);
                }
            };
            ret.addPlugin(new PlainAuthenticationHandler(validator));
            ret.addPlugin(new LoginAuthenticationHandler(validator));
            return ret;
        }
    }

    /**
     *
     */
    private class MessageReceiver implements MessageListener {

        /**
         * Always accept everything
         */
        public boolean accept(String from, String recipient) {
            return true;
        }

        /**
         * Cache the messages in memory
         */
        public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            data = new BufferedInputStream(data);

            // read the data from the stream
            int current;
            while ((current = data.read()) >= 0) {
                out.write(current);
            }

            synchronized (this) {
                if (!messages.containsKey(recipient)) {
                    messages.put(recipient, Lists.<MailMessage>newArrayList());
                }

                messages.get(recipient).add(new MailMessage(from, recipient, out.toByteArray()));
            }
        }
    }
}
