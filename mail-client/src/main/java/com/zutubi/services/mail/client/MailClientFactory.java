package com.zutubi.services.mail.client;

/**
 *
 */
public interface MailClientFactory {

    MailClient createClient(String connectionString);

    boolean isSupported(String connectionString);
}
