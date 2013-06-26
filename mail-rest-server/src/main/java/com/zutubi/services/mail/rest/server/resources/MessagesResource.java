package com.zutubi.services.mail.rest.server.resources;

import static com.google.common.collect.Lists.newArrayList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;

import com.zutubi.services.mail.api.MailMessage;
import com.zutubi.services.mail.core.server.MailServer;

/**
 *
 */
@Path("/accounts/{email}")
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class MessagesResource {

    private MailServer mailServer;

    @GET
    @Path("/messages")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public List<MailMessage> retrieveMessages() {
        List<MailMessage> messages = newArrayList();
        messages.add(new MailMessage("sender1", "receiverA", new byte[0]));
        messages.add(new MailMessage("sender2", "receiverB", new byte[0]));
        messages.add(new MailMessage("sender3", "receiverC", new byte[0]));
        return messages;
    }

    @Autowired
    public void setMailServer(MailServer mailServer) {
        this.mailServer = mailServer;
    }
}