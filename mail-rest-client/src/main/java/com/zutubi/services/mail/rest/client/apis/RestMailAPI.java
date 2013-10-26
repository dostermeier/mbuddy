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

import com.zutubi.services.mail.api.Account;
import com.zutubi.services.mail.api.MailAPI;
import com.zutubi.services.mail.api.MailMessage;

/**
 *
 */
public class RestMailAPI extends AbstractRestAPI implements MailAPI {

    public RestMailAPI(Client client, String baseUri) {
        super(client, baseUri);
    }

    @Override public void deleteMessages() {
        URI target = fromUri(Endpoints.MESSAGES).buildFromEncoded(getBaseUri());

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation delete = webTarget.request().buildDelete();

        // Invoke the request.
        Response response = delete.invoke();
        throwExceptionOnError(response);
    }

    @Override public void deleteAccount(String account) {
        URI target = fromUri(Endpoints.ACCOUNT).buildFromEncoded(getBaseUri(), account);

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation delete = webTarget.request().buildDelete();

        // Invoke the request.
        Response response = delete.invoke();
        throwExceptionOnError(response);
    }

    @Override public Account getAccount(String email) {
        URI target = fromUri(Endpoints.ACCOUNT).buildFromEncoded(getBaseUri(), email);

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        throwExceptionOnError(response);

        return response.readEntity(Account.class);
    }

    @Override public List<Account> getAccounts() {
        URI target = fromUri(Endpoints.ACCOUNTS).buildFromEncoded(getBaseUri());

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        throwExceptionOnError(response);

        return response.readEntity(new GenericType<List<Account>>(){});
    }

    @Override
    public List<MailMessage> getMessages(String account) {
        URI target = fromUri(Endpoints.ACCOUNT_MESSAGES).buildFromEncoded(getBaseUri(), account);

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        throwExceptionOnError(response);

        return response.readEntity(new GenericType<List<MailMessage>>(){});
    }

    @Override public MailMessage getMessage(UUID uuid) {
        URI target = fromUri(Endpoints.MESSAGE).buildFromEncoded(getBaseUri(), uuid);

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        throwExceptionOnError(response);

        return response.readEntity(MailMessage.class);
    }

    @Override public List<MailMessage> getMessages() {

        URI target = fromUri(Endpoints.MESSAGES).buildFromEncoded(getBaseUri());

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation get = webTarget.request().buildGet();

        // Invoke the request.
        Response response = get.invoke();
        throwExceptionOnError(response);

        return response.readEntity(new GenericType<List<MailMessage>>(){});
    }

    @Override public void deleteMessage(UUID uuid) {
        URI target = fromUri(Endpoints.MESSAGE).buildFromEncoded(getBaseUri(), uuid);

        WebTarget webTarget = getClient().target(target);

        // Configure the request / response characteristics.
        Invocation delete = webTarget.request().buildDelete();

        // Invoke the request.
        Response response = delete.invoke();
        throwExceptionOnError(response);
    }

    private <T> T throwExceptionOnError(Response response) {
        if (response.getStatus() >= 300) {
            throw new RuntimeException(response.toString());
        }
        return null;
    }
}
