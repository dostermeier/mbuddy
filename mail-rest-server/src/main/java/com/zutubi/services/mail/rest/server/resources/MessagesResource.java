package com.zutubi.services.mail.rest.server.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.List;
import java.util.UUID;

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
@Path("/messages")
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class MessagesResource {

    private MailServer mailServer;

    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public List<MailMessage> retrieveMessages() {
        return mailServer.getMessages();
    }

    @GET
    @Path("/{id}")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public MailMessage retrieveMessage(@PathParam("id") UUID id) {
        return mailServer.getMessage(id);
    }

    @Autowired
    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }
}