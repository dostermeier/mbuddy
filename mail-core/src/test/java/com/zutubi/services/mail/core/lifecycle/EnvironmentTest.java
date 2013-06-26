package com.zutubi.services.mail.core.lifecycle;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.zutubi.services.mail.test.testng.RulesListener;
import com.zutubi.services.mail.test.testng.annotations.TestNGRule;
import com.zutubi.services.mail.test.testng.rules.ResetSystemProperties;

@Listeners({RulesListener.class})
public class EnvironmentTest {

    private Environment env;

    @TestNGRule
    public ResetSystemProperties resetSystem = new ResetSystemProperties();

    @BeforeMethod
    public void setUp() {
        env = new Environment();
    }

    @Test
    public void testPropertyFromFile() {
        assertThat(env.getProperty("param.a"), is("a"));
    }

    @Test
    public void testPropertyFromSystem() {
        assertThat(env.getProperty("param.system"), is(nullValue()));
        System.setProperty("param.system", "value");
        assertThat(env.getProperty("param.system"), is("value"));
    }

    @Test
    public void testPropertyFromPut() {
        assertThat(env.getProperty("param.manual"), is(nullValue()));
        env.setRuntimeProperty("param.manual", "manual");
        assertThat(env.getProperty("param.manual"), is("manual"));
    }

    @Test
    public void testPrecedence() {
        assertThat(env.getProperty("param.a"), is("a"));

        env.setRuntimeProperty("param.a", "manual");
        assertThat(env.getProperty("param.a"), is("manual"));

        System.setProperty("param.a", "system");
        assertThat(env.getProperty("param.a"), is("system"));
    }

    @Test
    public void testResourceBaseUsesHome() {
        env.setHome(new File("/some/path/"));
        assertThat(env.getResourceBase(), containsString("some/path/system/www"));
    }

    @Test
    public void testDefaultTmpLocationTakenFromSystem() throws IOException {
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        assertThat(env.getTmp(),  is(tmp));
    }
}
