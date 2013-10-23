package com.zutubi.services.mail.api;

import java.util.List;
import java.util.UUID;

/**
 *
 */
public interface MailAPI {

    List<String> getAccounts();

    List<MailMessage> getMessages(String account);

    MailMessage getMessage(UUID uuid);

    List<MailMessage> getMessages();
}
