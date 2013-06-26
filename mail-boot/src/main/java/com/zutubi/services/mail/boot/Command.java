package com.zutubi.services.mail.boot;

import java.util.List;
import java.util.Map;

/**
 * The Command interface is the base API definition for components (commands)
 * that can be run command line requests via the {@link BootControl}.
 */
public interface Command {

    /**
     * A command should implement this method to carry out its' actions.
     *
     * @param context the context in which this command is being executed.
     * @return the exit code of this command.
     * @throws CommandException is thrown on error.
     */
    public int execute(BootContext context) throws CommandException;

    /**
     * Get a short help message.  This is displayed when a user requests general
     * help about the available commands.
     *
     * @return a short help message.
     */
    public String getHelp();

    /**
     * Get a detailed help message.  This is displayed when a user requests command
     * specific help.
     *
     * @return a detailed help message.
     */
    public String getDetailedHelp();

    /**
     * Get a list of sample usages.
     *
     * @return sample usages.
     */
    public List<String> getUsages();

    /**
     * Get a map of the available command options.  The key of the map is the command
     * line flag used to trigger the option and the value is the description of the
     * option.
     *
     * @return a map describing the commands options.
     */
    public Map<String, String> getOptions();
}
