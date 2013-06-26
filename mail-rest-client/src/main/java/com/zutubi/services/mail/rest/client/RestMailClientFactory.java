package com.zutubi.services.mail.rest.client;

import com.zutubi.services.mail.client.MailClient;
import com.zutubi.services.mail.client.MailClientFactory;

/**
 *
 */
public class RestMailClientFactory implements MailClientFactory {

    @Override
    public boolean isSupported(String connectionString) {
        ConnectionConfig con = ConnectionConfig.parse(connectionString);
        return con.getProtocol().compareTo("rest") == 0;
    }

    @Override
    public MailClient createClient(String connectionString) {
        ConnectionConfig con = ConnectionConfig.parse(connectionString);
        return new RestMailClient(con.getBaseURI(), con.getParams());
    }
}
