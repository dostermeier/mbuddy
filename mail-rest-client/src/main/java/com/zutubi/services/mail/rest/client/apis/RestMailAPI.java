package com.zutubi.services.mail.rest.client.apis;

import static javax.ws.rs.core.UriBuilder.fromUri;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;

/**
 *
 */
public class RestMailAPI extends AbstractRestAPI implements MailAPI {

    public RestMailAPI(Client client, String baseUri) {
        super(client, baseUri);
    }

    @Override public List<String> getAccounts() {
        URI baseUri = URI.create(getBaseUri());
        URI target = fromUri(baseUri).path("accounts.json").build();

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        if (response.getStatus() != 200) {
            throw new RuntimeException(response.toString());
        }

        return response.readEntity(new GenericType<List<String>>(){});
    }

    @Override
    public List<MailMessage> getMessages(String account) {

        // Build the Resource URI.
        URI baseUri = URI.create(getBaseUri());
        URI target = fromUri(baseUri).path("accounts").path(account).path("messages.json").build();

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        if (response.getStatus() != 200) {
            throw new RuntimeException(response.toString());
        }

        return response.readEntity(new GenericType<List<MailMessage>>(){});
    }

    @Override public MailMessage getMessage(UUID uuid) {
        URI baseUri = URI.create(getBaseUri());
        URI target = fromUri(baseUri).path("messages").path(uuid.toString() + ".json").build();

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        if (response.getStatus() != 200) {
            throw new RuntimeException(response.toString());
        }

        return response.readEntity(MailMessage.class);
    }

    @Override public List<MailMessage> getMessages() {

        URI baseUri = URI.create(getBaseUri());
        URI target = fromUri(baseUri).path("messages.json").build();

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        if (response.getStatus() != 200) {
            throw new RuntimeException(response.toString());
        }

        return response.readEntity(new GenericType<List<MailMessage>>(){});
    }
}
