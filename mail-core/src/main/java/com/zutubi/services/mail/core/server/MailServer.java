package com.zutubi.services.mail.core.server;

import static com.google.common.collect.Lists.newArrayList;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.auth.LoginAuthenticationHandler;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.PlainAuthenticationHandler;
import org.subethamail.smtp.auth.PluginAuthenticationHandler;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.MessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zutubi.services.mail.api.Account;
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
    private Map<String, List<MailMessage>> messagesByAccount = Collections.synchronizedMap(Maps.<String, List<MailMessage>>newHashMap());
    private Map<UUID, MailMessage> messagesById = Collections.synchronizedMap(Maps.<UUID, MailMessage>newHashMap());

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

    @Override public List<MailMessage> getMessages() {
        return newArrayList(messagesById.values());
    }

    @Override public List<MailMessage> getMessages(String account) {
        List<MailMessage> accountMessages = messagesByAccount.get(account);
        if (accountMessages != null) {
            return accountMessages;
        }
        return newArrayList();
    }

    @Override public MailMessage getMessage(UUID uuid) {
        return messagesById.get(uuid);
    }

    @Override public void deleteMessage(UUID uuid) {
        messagesById.remove(uuid);
    }

    @Override public void deleteMessages() {
        messagesById.clear();
        for (String account : messagesByAccount.keySet()) {
            messagesByAccount.get(account).clear();
        }
    }

    @Override public void deleteAccount(String account) {
        if (messagesByAccount.containsKey(account)) {
            List<MailMessage> messages = messagesByAccount.get(account);
            for (MailMessage message : messages) {
                messagesById.remove(message.getId());
            }
            messagesByAccount.remove(account);
        }
    }

    @Override public Account getAccount(String email) {
        if (messagesByAccount.containsKey(email)) {
            return new Account(email);
        }
        return null;
    }

    @Override public List<Account> getAccounts() {
        List<Account> accounts = newArrayList();
        for (String email : messagesByAccount.keySet()) {
            accounts.add(new Account(email));
        }
        return accounts;
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
        public void deliver(String from, String recipient, InputStream data) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            data = new BufferedInputStream(data);

            // read the data from the stream
            int current;
            while ((current = data.read()) >= 0) {
                out.write(current);
            }

            synchronized (this) {
                if (!messagesByAccount.containsKey(recipient)) {
                    messagesByAccount.put(recipient, Lists.<MailMessage>newArrayList());
                }

                MailMessage mailMessage = new MailMessage(from, recipient, out.toByteArray());
                messagesByAccount.get(recipient).add(mailMessage);
                messagesById.put(mailMessage.getId(), mailMessage);
            }
        }
    }
}
