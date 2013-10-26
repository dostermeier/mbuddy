package com.zutubi.services.mail.rest.client.apis;

/**
 *
 */
public final class Endpoints {

    private Endpoints() {

    }

    public static final String ACCOUNTS = "{base}/accounts.json";
    public static final String ACCOUNT = "{base}/accounts/{id}.json";
    public static final String ACCOUNT_MESSAGES = "{base}/accounts/{id}/messages.json";

    public static final String MESSAGES = "{base}/messages.json";
    public static final String MESSAGE = "{base}/messages/{id}.json";
}
