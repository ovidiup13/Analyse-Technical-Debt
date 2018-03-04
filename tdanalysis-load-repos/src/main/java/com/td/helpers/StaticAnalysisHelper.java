package com.td.helpers;

import com.td.models.BugModel;
import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StaticAnalysisHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticAnalysisHelper.class);

    private static final String JAR_EXTENSION = ".jar";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private static final String COMMAND_LINUX = "spotbugs";
    private static final String COMMAND_WINDOWS = "spotbugs.bat";

    private static final String COMMAND_LINE = "-textui";
    private static final String PRIORITY = "-high";

    private String findbugsPath;

    public StaticAnalysisHelper(String analyserPath) {
        this.findbugsPath = analyserPath;
    }

    /***
     * Executes the analysis for all project JARs found in the directory.
     * @param repositoryModel the repository model object
     * @return list of bugs
     * @throws IOException if the program is not found
     * @throws InterruptedException if any of the processes is interrupted.
     */
    public List<BugModel> executeAnalysis(RepositoryModel repositoryModel) throws IOException, InterruptedException {

        LOGGER.info(String.format("Starting analysis for project %s:%s", repositoryModel.getName(),
                repositoryModel.getAuthor()));

        Set<BugModel> results = Collections.synchronizedSet(new HashSet<>());
        String analysisCommand = System.getProperty("os.name").contains("Windows") ? COMMAND_WINDOWS : COMMAND_LINUX;
        List<String> projectJars = getProjectJars(repositoryModel.getProjectFolder(), repositoryModel.getName());

        // process JARs in parallel to speed up analysis
        projectJars.parallelStream().forEach(jar -> {
            LOGGER.info(String.format("Starting analysis for JAR %s in project %s", jar, repositoryModel.getName()));
            try {
                results.addAll(analyseJar(analysisCommand, repositoryModel.getProjectFolder(), jar));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        return new ArrayList<>(results);
    }

    /***
     * Method that starts a new process and analyses a JAR within the project directory.
     * @param command command to run the analysis
     * @param projectDirectory project... directory
     * @param jarPath jar... path
     * @return list of bugs
     * @throws IOException if the program is not found
     * @throws InterruptedException if the process is interrupted
     */
    private Set<BugModel> analyseJar(String command, File projectDirectory, String jarPath)
            throws IOException, InterruptedException {

        Set<BugModel> results = new HashSet<>();
        ProcessBuilder builder = new ProcessBuilder();

        // set up process
        builder.command(command, COMMAND_LINE, PRIORITY, jarPath);
        builder.directory(projectDirectory);

        // make sure findbugs is in process path
        Map<String, String> envs = builder.environment();
        envs.put("PATH", findbugsPath + File.pathSeparator + System.getenv("PATH"));

        Process p = builder.start();

        // store the results as they are found
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            Optional<BugModel> optional = parseBug(line);
            optional.ifPresent(results::add);
        }

        // wait for process to complete
        p.waitFor();

        return results;
    }

    private Optional<BugModel> parseBug(String line) {

        BugModel bug = new BugModel();
        int index = line.indexOf(":");

        if (index < 0) {
            return Optional.empty();
        }

        bug.setType(line.substring(0, index - 1));
        bug.setText(line.substring(index + 1));
        bug.setFullText(line);

        return Optional.of(bug);
    }

    /**
     * Returns all the JARs that contain the name of the project.
     *
     * @param projectFolder folder of the build files
     * @param projectName   the name of the project
     * @return a list of all the jars in the form of absolute paths
     * @throws IOException if the project folder does not exist
     */
    private List<String> getProjectJars(File projectFolder, String projectName) throws IOException {
        return Files.walk(Paths.get(projectFolder.getAbsolutePath())).filter(Files::isRegularFile)
                .filter(path -> isProjectJar(path, projectName)).map(Path::toString).collect(Collectors.toList());
    }

    private static boolean isProjectJar(Path path, String projectName) {
        String fileName = path.getFileName().toString();
        return fileName.contains(projectName) && isJarFile(fileName) && !isSourcesJar(fileName) && !isDocsJar(fileName)
                && !isTestsJar(fileName);
    }

    private static boolean isJarFile(String fileName) {
        int index = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return index > 0 && fileName.substring(index).equals(JAR_EXTENSION);
    }

    private static boolean isSourcesJar(String fileName) {
        return fileName.contains("sources");
    }

    private static boolean isDocsJar(String fileName) {
        return fileName.contains("doc");
    }

    private static boolean isTestsJar(String fileName) {
        return fileName.contains("test");
    }
}
