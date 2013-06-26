package com.zutubi.services.mail.rest.client.apis;

import javax.ws.rs.client.Client;

/**
 *
 */
public class AbstractRestAPI {

    private Client client;
    private String baseUri;

    public AbstractRestAPI(Client client, String baseUri) {
        this.client = client;
        this.baseUri = baseUri;
    }

    protected final Client getClient() {
        return client;
    }

    protected final String getBaseUri() {
        return baseUri;
    }
}
