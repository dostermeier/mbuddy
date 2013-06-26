package com.zutubi.services.mail.client;

import java.util.ServiceLoader;

/**
 *
 */
public class MailClientManager {

    public static MailClient getClient(String connectionUrl) {

        if (!connectionUrl.startsWith("mail:")) {
            throw new IllegalArgumentException("Unsupported service protocol");
        }

        String remainder = connectionUrl.substring(5);

        ServiceLoader<MailClientFactory> serviceLoader = ServiceLoader.load(MailClientFactory.class);
        for (MailClientFactory factory : serviceLoader) {
            if (factory.isSupported(remainder)) {
                return factory.createClient(remainder);
            }
        }
        return null;
    }
}
