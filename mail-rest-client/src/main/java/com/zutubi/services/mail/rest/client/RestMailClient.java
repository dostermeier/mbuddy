package com.zutubi.services.mail.rest.client;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.client.AbstractMailClient;
import com.zutubi.services.mail.rest.client.apis.RestMailAPI;

/**
 *
 */
public class RestMailClient extends AbstractMailClient {

    private String baseUri;
    private final Client client;

    public RestMailClient(String baseUri, Map<String, String> params) {

        this.baseUri = "http://" + baseUri;

        ClientConfig config = new ClientConfig();
        config.register(JacksonFeature.class);
        config.register(new LoggingFilter());
        client = ClientBuilder.newClient(config);
    }

    @Override
    public MailAPI getMailAPI() {
        return new RestMailAPI(client, baseUri);
    }
}
