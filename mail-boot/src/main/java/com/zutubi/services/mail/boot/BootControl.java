package com.zutubi.services.mail.boot;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zutubi.services.mail.boot.cli.HelpCommand;

/**
 * The Control object is responsible for bootstrapping the application.
 * <p/>
 * It does so in three steps
 * <ul>
 * <li>Create the root ClassLoader based on the classpath.txt.  By doing this within this
 * class we save the startup scripts from having to manage these details.</li>
 * <li>Find the available commands based on classpath:/commands.properties files</li>
 * <li>Execute the requested command, or the help command if we encounter any problems.</li>
 * </ul>
 * <p/>
 * If -Ddebug is specified on java command line then this class will display debugging
 * information.
 */
public class BootControl {

    public static final String APP_HOME = "app.home";
    public static final String DEBUG = "debug";
    private static final String JAVA_HOME = "java.home";
    private static final String TOOLS_JAR = "tools.jar";

    public static final int CONFIGURATION_ERROR = 1;
    public static final int UNEXPECTED_ERROR = 2;
    public static final int INVOCATION_ERROR = 3;

    private static final String CLASSPATH_TXT = "classpath.txt";
    private static final String COMMANDS_PROPERTIES = "commands.properties";

    /**
     * The entry point into the MailControl.
     *
     * @param argv the command line arguments
     * @return the exit code of the run command.
     */
    public int process(String... argv) {
        try {
            File home = getHome();

            URLClassLoader classpath = makeClassLoader(home);

            Map<String, Command> commands = loadCommands(classpath);

            BootContext context = new BootContext(classpath, commands, argv, home);

            if (argv.length == 0) {
                return runHelpCommand(commands, context);
            }

            String commandName = argv[0];

            if (!commands.containsKey(commandName)) {
                printError("Unknown command: " + commandName);
                runHelpCommand(commands, context);
                return INVOCATION_ERROR;
            }

            Thread.currentThread().setContextClassLoader(classpath);

            Command command = commands.get(commandName);
            return command.execute(context);

        } catch (SystemExitRequest e) {
            return e.getExitCode();

        } catch (Exception e) {
            printError(e);
            return UNEXPECTED_ERROR;
        }
    }

    private int runHelpCommand(Map<String, Command> commands, BootContext context) throws CommandException {
        for (Command command : commands.values()) {
            if (command instanceof HelpCommand) {
                return command.execute(context);
            }
        }

        printError("Unrecognised command, and no help command specified");
        return INVOCATION_ERROR;
    }

    private File getHome() {
        String homeString = System.getProperty(APP_HOME);
        if (homeString == null) {
            printError("Require property '" + APP_HOME + "' not set");
            throw new SystemExitRequest(CONFIGURATION_ERROR);
        }

        File home = new File(homeString);
        if (!home.isDirectory()) {
            printError("Invalid home '" + home.getAbsolutePath() + "'");
            throw new SystemExitRequest(CONFIGURATION_ERROR);
        }
        return home;
    }

    private Map<String, Command> loadCommands(URLClassLoader classpath) throws URISyntaxException, IOException {
        Map<String, Command> commands = new HashMap<String, Command>();

        Properties commandClasses = new Properties();

        // look through the elements on the classpath for command.properties files.
        for (URL classpathEntry : classpath.getURLs()) {
            File file = new File(classpathEntry.toURI());
            if (file.isDirectory()) {
                File commandProperties = new File(file, COMMANDS_PROPERTIES);
                if (commandProperties.isFile()) {
                    FileInputStream in = new FileInputStream(commandProperties);
                    readCommandProperties(commandClasses, in);
                }
            } else if (file.isFile()) {
                ZipFile zip = null;
                try {
                    zip = new ZipFile(file);
                    ZipEntry entry = zip.getEntry(COMMANDS_PROPERTIES);
                    if (entry != null) {
                        readCommandProperties(commandClasses, zip.getInputStream(entry));
                    }
                } catch (IOException e) {
                    // not a zip file.
                } finally {
                    if (zip != null) {
                        zip.close();
                    }
                }
            }
        }

        for (Map.Entry entry : commandClasses.entrySet()) {
            addCommand(commands, classpath, (String) entry.getKey(), (String) entry.getValue());
        }

        return commands;
    }

    private void readCommandProperties(Properties commandClasses, InputStream in) {
        try {
            commandClasses.load(in);
        } catch (IOException e) {
            printError(e);
            throw new SystemExitRequest(UNEXPECTED_ERROR);
        } finally {
            closeQuietly(in);
        }
    }

    private void addCommand(Map<String, Command> commands, ClassLoader classpath, String name, String className) {
        try {
            Command command = (Command) classpath.loadClass(className).newInstance();
            commands.put(name, command);
        } catch (Exception e) {
            printError("Unable to load command '" + name + "'");
            printError(e);
            throw new SystemExitRequest(CONFIGURATION_ERROR);
        }
    }

    private void printError(String msg) {
        System.err.println("Error: " + msg);
    }

    private void printError(Exception e) {
        e.printStackTrace(System.err);
        System.err.println("Error: " + e.getMessage());
    }

    private static boolean isDebugEnabled() {
        return Boolean.getBoolean(DEBUG);
    }

    public static void main(String[] argv) {
        int exitStatus = new BootControl().process(argv);
        if (exitStatus != 0) {
            System.exit(exitStatus);
        }
    }

    private static URLClassLoader makeClassLoader(File home) throws IOException {
        ClassLoader parent = BootControl.class.getClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }

        File classpathFile = new File(home, asPath(new String[]{"bin", CLASSPATH_TXT}));
        if (!classpathFile.exists()) {
            throw new IOException("Classpath file '" + classpathFile.getAbsolutePath() + "' does not exist");
        }

        List<URL> classpath = new ArrayList<URL>();
        BufferedReader reader = new BufferedReader(new FileReader(classpathFile));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                addToClasspath(home, line, classpath);
            }
        } finally {
            closeQuietly(reader);
        }

        addToolsJar(classpath);

        URL[] urls = classpath.toArray(new URL[classpath.size()]);
        if (isDebugEnabled()) {
            System.err.println("Classpath:");
            for (URL url : urls) {
                System.err.println(" - " + url);
            }
        }

        return new URLClassLoader(urls, parent);
    }

    private static void addToClasspath(File home, String entry, List<URL> classpath) throws IOException {
        entry = entry.trim();
        if (entry.length() == 0) {
            return;
        }

        if (entry.endsWith("/")) {
            classpath.add(new File(home, normaliseSeparators(entry)).toURL());
        } else {
            int index = entry.lastIndexOf('/');
            File dir;
            if (index > 0) {
                dir = new File(home, normaliseSeparators(entry.substring(0, index)));
            } else {
                dir = home;
            }
            String pattern;
            if (index < entry.length() - 1) {
                pattern = entry.substring(index + 1);
            } else {
                throw new IOException("Invalid classpath entry '" + entry + "'");
            }
            final Pattern regex = Pattern.compile(pattern);
            String[] files = dir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return regex.matcher(name).matches();
                }
            });
            for (String file : files) {
                classpath.add(new File(dir, file).toURL());
            }
        }
    }

    private static String normaliseSeparators(String entry) {
        return entry.replace('/', File.separatorChar);
    }

    private static String asPath(String[] elements) {
        StringBuilder buffer = new StringBuilder();
        String sep = "";
        for (String element : elements) {
            buffer.append(sep);
            buffer.append(element);
            sep = File.separator;
        }
        return buffer.toString();
    }

    private static void addToolsJar(List<URL> jarUrls) throws MalformedURLException {
        File javaHome = new File(System.getProperty(JAVA_HOME));

        File tools = new File(javaHome, asPath(new String[]{"lib", TOOLS_JAR}));
        if (tools.isFile()) {
            jarUrls.add(tools.toURL());
            return;
        }

        tools = new File(javaHome, asPath(new String[]{"..", "lib", TOOLS_JAR}));
        if (tools.isFile()) {
            jarUrls.add(tools.toURL());
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // ignore.
        }
    }

    private static class SystemExitRequest extends RuntimeException {
        private int exitCode;

        private SystemExitRequest(int exitCode) {
            this.exitCode = exitCode;
        }

        public int getExitCode() {
            return exitCode;
        }
    }
}
