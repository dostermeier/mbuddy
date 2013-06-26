package com.zutubi.services.mail.boot.cli;

import java.util.List;
import java.util.Map;

import com.zutubi.services.mail.boot.BootContext;
import com.zutubi.services.mail.boot.Command;

/**
 * A noop command implementation.
 */
public class SampleCommand implements Command {
    @Override public int execute(BootContext paramBootContext) {
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
