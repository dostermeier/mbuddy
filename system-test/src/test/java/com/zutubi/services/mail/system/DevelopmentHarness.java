package com.zutubi.services.mail.system;

import com.zutubi.services.mail.system.resources.MailAppServerResource;

/**
 * This development harness allows the application to be run from within an
 * IDE.
 */
public class DevelopmentHarness {

    public static void main(String... argv) throws Throwable {
        MailAppServerResource appServer = new MailAppServerResource();
        appServer.before();
        Thread.sleep(Integer.MAX_VALUE);
        appServer.after();
    }
}
