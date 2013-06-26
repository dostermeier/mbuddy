package com.zutubi.services.mail.boot;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;

/**
 * The boot context contains details relating to the io applications
 * boot environment.  This environment is provided to each command
 * during its execution.
 */
public class BootContext {

    /**
     * The root class loader being used.
     */
    private URLClassLoader classLoader;

    /**
     * A map of all the available commands keyed by the name used to reference
     * those commands.
     */
    private Map<String, Command> commands;

    /**
     * The command line arguments.
     */
    private String[] argv;

    /**
     * The applications home directory.
     */
    private File home;

    public BootContext(URLClassLoader classLoader, Map<String, Command> commands, String[] argv, File home) {
        this.classLoader = classLoader;
        this.commands = commands;
        this.home = home;
        this.argv = new String[argv.length];
        System.arraycopy(argv, 0, this.argv, 0, argv.length);
    }

    public URLClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Map<String, Command> getCommands() {
        return this.commands;
    }

    /**
     * Get the command line arguments.
     *
     * @return the command line arguments.
     */
    public String[] getArgv() {
        if (argv == null) {
            return null;
        } else {
            return Arrays.copyOf(argv, argv.length);
        }
    }

    /**
     * Get the portion of the command line arguments specific to
     * the command being executed.
     *
     * @return the commands arguments.
     */
    public String[] getCommandArgv() {
        if (this.argv.length == 0) {
            return new String[0];
        }

        String[] commandArgs = new String[this.argv.length - 1];
        System.arraycopy(this.argv, 1, commandArgs, 0, commandArgs.length);
        return commandArgs;
    }

    /**
     * Get the applications home directory.
     *
     * @return the home directory.
     */
    public File getHome() {
        return this.home;
    }
}
