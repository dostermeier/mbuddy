package com.zutubi.services.mail.boot;

import static com.zutubi.services.mail.boot.BootControl.CONFIGURATION_ERROR;
import static com.zutubi.services.mail.boot.BootControl.DEBUG;
import static com.zutubi.services.mail.boot.BootControl.INVOCATION_ERROR;
import static com.zutubi.services.mail.boot.BootControl.APP_HOME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BootControlTest {

    private BootControl control;

    @BeforeMethod
    public void setUp() {
        control = new BootControl();
    }

    @AfterMethod
    public void tearDown() {
        System.getProperties().remove(APP_HOME);
        System.getProperties().remove(DEBUG);
    }

    @Test
    public void testHomeNotSet() {
        assertThat(control.process(), is(CONFIGURATION_ERROR));
    }

    @Test
    public void testHomeInvalid() throws IOException {
        setMailHome(new File("/invalid/path"));
        assertThat(control.process(), is(CONFIGURATION_ERROR));
    }

    @Test
    public void testDefaultCommand() throws IOException {
        setMailHome(testResourceDir());
        assertThat(control.process(), is(0));
    }

    @Test
    public void testSampleCommand() throws IOException {
        setMailHome(testResourceDir());
        assertThat(control.process("sample"), is(0));
    }

    @Test
    public void testUnknownCommand() throws IOException {
        setMailHome(testResourceDir());
        assertThat(control.process("unknown"), is(INVOCATION_ERROR));
    }

    @Test
    public void testDebug() throws IOException {
        enableDebug();
        setMailHome(testResourceDir());
        assertThat(control.process("sample"), is(0));
    }

    @Test
    public void testBootContext() throws IOException {
        setMailHome(testResourceDir());
        assertThat(control.process("check", "arg"), is(0));
    }

    private void enableDebug() {
        System.setProperty(DEBUG, "true");
    }

    private void setMailHome(File file) throws IOException {
        System.setProperty(APP_HOME, file.getCanonicalPath());
    }

    private File testResourceDir() {
        return new File(projectHome(), "mail-boot/src/test/resources");
    }

    private File projectHome() {
        File basePom = new File("../pom.xml");
        if (!basePom.exists()) {
            basePom = new File("./pom.xml");
        }

        return basePom.getParentFile();
    }
}
