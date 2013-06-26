package com.zutubi.services.mail.client;

import com.zutubi.services.mail.api.MailAPI;

/**
 * A client is the base interface for all remote clients implementations. It
 * defines a common set of APIs that allow a remote process access the constructs
 * and data within the application.
 */
public interface MailClient {

    MailAPI getMailAPI();
}
