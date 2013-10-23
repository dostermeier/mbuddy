package com.zutubi.services.mail.system;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.zutubi.services.mail.system.resources.MailAppServerResource;

/**
 * This development harness allows the application to be run from within an
 * IDE.  Simply execute the {@link DevelopmentHarness#freshInstance()} test case.
 */
public class DevelopmentHarness {

    private final MailAppServerResource APP_SERVER = new MailAppServerResource();

    @BeforeSuite
    public void beforeSuite() throws Throwable {
        APP_SERVER.before();
    }

    @AfterSuite
    public void afterSuite() throws Throwable {
        APP_SERVER.after();
    }

    @Test
    public void freshInstance() throws Exception {
        Thread.sleep(Integer.MAX_VALUE);
    }
}
