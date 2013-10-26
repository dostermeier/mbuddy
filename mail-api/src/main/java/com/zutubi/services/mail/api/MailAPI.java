package com.zutubi.services.mail.api;

import java.util.List;
import java.util.UUID;

/**
 *
 */
public interface MailAPI {

    void deleteAccount(String account);

    Account getAccount(String email);

    List<Account> getAccounts();

    void deleteMessages();

    List<MailMessage> getMessages(String account);

    MailMessage getMessage(UUID uuid);

    List<MailMessage> getMessages();

    void deleteMessage(UUID uuid);
}
