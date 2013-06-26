package com.zutubi.services.mail.boot.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Map;

import com.zutubi.services.mail.boot.BootContext;
import com.zutubi.services.mail.boot.Command;

/**
 * A command implementation that runs some checks against the provided boot context instance.
 */
public class CheckContextCommand implements Command {

    @Override public int execute(BootContext context) {

        assertThat(context.getHome().isDirectory(), is(true));
        assertThat(context.getCommands().keySet(), hasItems("sample", "check", "help"));
        assertThat(context.getArgv(), is(new String[]{"check", "arg"}));
        assertThat(context.getCommandArgv(), is(new String[]{"arg"}));
        assertThat(context.getClassLoader(), is(not(nullValue())));
        return 0;
    }

    @Override public String getHelp() {
        return null;
    }

    @Override public String getDetailedHelp() {
        return null;
    }

    @Override public List<String> getUsages() {
        return null;
    }

    @Override public Map<String, String> getOptions() {
        return null;
    }
}
