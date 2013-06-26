package com.zutubi.services.mail.api;

import java.util.List;

/**
 *
 */
public interface MailAPI {

    List<MailMessage> getMessages(String account);
}
