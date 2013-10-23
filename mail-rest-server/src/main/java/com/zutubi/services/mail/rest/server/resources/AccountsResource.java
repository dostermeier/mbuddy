package com.zutubi.services.mail.rest.server.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;

import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.core.server.MailServer;

/**
 *
 */
@Path("/accounts")
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class AccountsResource {

    private MailServer mailServer;

    @GET
    @Path("/")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public List<String> retrieveAccounts() {
        return mailServer.getAccounts();
    }

    @GET
    @Path("/{email}/messages")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public List<MailMessage> retrieveMessages(@PathParam("email") String email) {
        return mailServer.getMessages(email);
    }

    @Autowired
    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }
}
