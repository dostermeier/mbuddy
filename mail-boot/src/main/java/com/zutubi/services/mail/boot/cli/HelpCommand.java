package com.zutubi.services.mail.boot.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zutubi.services.mail.boot.BootContext;
import com.zutubi.services.mail.boot.Command;

/**
 * A command used to print documentation about the available commands.
 */
public class HelpCommand implements Command {

    @Override
    public int execute(BootContext context) {
        // With no args, or when help is invoked due to an unrecognised
        // command, show top level help.  With args, use arg name as
        // command to show help for.
        String[] argv = context.getArgv();
        if (argv.length <= 1) {
            showHelp(context.getCommands());
        } else {
            Command command = context.getCommands().get(argv[1]);
            if (command == null || command.getHelp() == null) {
                System.err.println("Unrecognised command '" + argv[1] + "'");
                return 1;
            } else {
                showHelp(argv[1], command);
            }
        }
        return 0;
    }

    @Override
    public String getHelp() {
        return "show command documentation";
    }

    @Override
    public String getDetailedHelp() {
        return "Displays usage and parameter information for available commands.";
    }

    @Override
    public List<String> getUsages() {
        return Arrays.asList("[ <command> ]");
    }

    @Override
    public Map<String, String> getOptions() {
        return null;
    }

    private void showHelp(Map<String, Command> commands) {
        System.out.println("Usage: mail <command> [ <option> ... ] [ <arg> ... ]");
        System.out.println();
        System.out.println("Available commands:");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            Command command = entry.getValue();
            if (command.getHelp() != null) {
                System.out.println(String.format("  %-16s: %s", entry.getKey(), entry.getValue().getHelp()));
            }
        }
        System.out.println();
        System.out.println("For help on a specific command, type 'io help <command>'");
    }

    private void showHelp(String name, Command command) {
        System.out.println(name + ": " + command.getHelp());

        String usageString = "Usage:";
        for (String usage : command.getUsages()) {
            System.out.println(String.format("%s %s %s", usageString, name, usage));
            usageString = "      ";
        }

        System.out.println();
        System.out.println(command.getDetailedHelp());

        Map<String, String> options = command.getOptions();
        if (options != null) {
            System.out.println();
            System.out.println("Available options:");
            for (Map.Entry<String, String> option : options.entrySet()) {
                System.out.println(String.format("%-26s: %s", option.getKey(), option.getValue()));
            }
        }

        System.out.println();
    }
}