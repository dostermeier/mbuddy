package com.zutubi.services.mail.core.lifecycle;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.zutubi.services.mail.test.testng.RulesListener;
import com.zutubi.services.mail.test.testng.annotations.TestNGRule;
import com.zutubi.services.mail.test.testng.rules.TemporaryFolder;

@Listeners({RulesListener.class})
public class ApplicationTest {

    @TestNGRule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Application application;

    @BeforeMethod
    public void setUp() throws IOException {
        application = new Application();
        application.getEnvironment().setHome(tmp.newFolder());
    }

    @Test
    public void testStartAndStop() {
        assertThat(application.isRunning(), is(false));
        application.start();
        assertThat(application.isRunning(), is(true));
        application.stop();
        assertThat(application.isRunning(), is(false));
    }

    @Test
    public void testJoinWithTimeout() throws InterruptedException {

        application.start();

        double currentTime = currentTimeMillis();
        application.join(500);

        double waitTime = currentTimeMillis() - currentTime;
        assertThat(waitTime, closeTo(500, 100));

        application.stop();
    }
}
