package com.zutubi.services.mail.client;

import com.zutubi.services.mail.api.MailAPI;

/**
 *
 */
public class AbstractMailClient implements MailClient {

    @Override
    public MailAPI getMailAPI() {
        throw new UnsupportedOperationException();
    }
}
