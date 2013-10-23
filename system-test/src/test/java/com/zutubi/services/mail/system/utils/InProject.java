package com.zutubi.services.mail.system.utils;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Utility class for working with resources specific to this project.
 */
public class InProject {

    /**
     * Locate the canonical path for the webapp directory within the
     * io-rest-server module.
     *
     * @return the canonical path.
     */
    public static String locateRestWebApp() {
        return locateCanonical("**/mail-rest-server/src/main/webapp");
    }

    /**
     * Locate the canonical path for the webapp directory within the
     * io-web module.
     *
     * @return the canonical path.
     */
    public static String locateWebApp() {
        return locateCanonical("**/mail-web/src/main/webapp");
    }

    public static String locateCanonical(String path) {
        try {
            List<File> paths = locate(path);
            return paths.get(0).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> locate(String path) {

        // The jvm can be started in a number of locations.  From maven, it is where
        // the project pom is located.  From IDEA it is where the .idea directory is located.
        // So, to find a good starting place for our FS search, we find the root pom and start
        // from there.

        File basePom = new File("../pom.xml");
        if (!basePom.exists()) {
            basePom = new File("./pom.xml");
        }

        final File projectHome = basePom.getParentFile();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(projectHome);
        scanner.setIncludes(new String[]{path});
        scanner.scan();

        String[] includedFiles = scanner.getIncludedFiles();
        String[] includedDirectories = scanner.getIncludedDirectories();

        List<File> found = newArrayList();
        found.addAll(Lists.transform(Arrays.asList(includedFiles), new Function<String, File>() {
            @Override
            public File apply(String path) {
                return new File(projectHome, path);
            }
        }));
        found.addAll(Lists.transform(Arrays.asList(includedDirectories), new Function<String, File>() {
            @Override
            public File apply(String path) {
                return new File(projectHome, path);
            }
        }));
        return found;
    }
}
