package com.td.helpers.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.td.models.CommitTD;
import com.td.models.RepositoryModel;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtPriority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FindBugsAnalysisHelper implements StaticAnalysisHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindBugsAnalysisHelper.class);

    private static final String JAR_EXTENSION = ".jar";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    @Value("${findbugs.command.linux}")
    private String findBugsCommandLinux;

    @Value("${findbugs.command.windows}")
    private String findBugsCommandWindows;

    @Value("${findbugs.command.ui}")
    private String findBugsUIParam;

    @Value("${findbugs.command.priority}")
    private String findBugsPriority;

    @Value("${findbugs.home.path}")
    private String findBugsPath;

    /***
     * Executes the analysis for all project JARs found in the directory.
     * @param repositoryModel the repository model object
     * @return list of bugs
     * @throws IOException if the program is not found
     * @throws InterruptedException if any of the processes is interrupted.
     */
    public TechnicalDebt executeAnalysis(RepositoryModel repositoryModel) {

        LOGGER.info(String.format("Starting analysis for project %s:%s", repositoryModel.getName(),
                repositoryModel.getAuthor()));

        Set<CommitTD> results = Collections.synchronizedSet(new HashSet<>());
        String analysisCommand = System.getProperty("os.name").contains("Windows") ? findBugsCommandWindows
                : findBugsCommandLinux;
        List<String> projectJars = getProjectJars(repositoryModel.getProjectFolder(), repositoryModel.getName());

        // process JARs in parallel to speed up analysis
        projectJars.parallelStream().forEach(jar -> {
            LOGGER.info(String.format("Starting analysis for JAR %s in project %s", jar, repositoryModel.getName()));
            results.addAll(analyseJar(analysisCommand, repositoryModel.getProjectFolder(), jar));
        });

        return analyseResults(results);
    }

    TechnicalDebt analyseResults(Set<CommitTD> tdItems) {
        TechnicalDebt td = new TechnicalDebt();

        int highPriority = getPriorityCount(TechnicalDebtPriority.HIGH, tdItems);
        int mediumPriority = getPriorityCount(TechnicalDebtPriority.MEDIUM, tdItems);
        int lowPriority = getPriorityCount(TechnicalDebtPriority.LOW, tdItems);

        td.setTotalCount(tdItems.size());
        td.setHighCount(highPriority);
        td.setMediumCount(mediumPriority);
        td.setLowCount(lowPriority);
        td.setTdItems(new ArrayList<>(tdItems));

        return td;
    }

    int getPriorityCount(TechnicalDebtPriority priority, Set<CommitTD> tdItems) {
        return (int) tdItems.stream().filter(item -> item.getPriority().equals(priority)).count();
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
    Set<CommitTD> analyseJar(String command, File projectDirectory, String jarPath) {

        Set<CommitTD> results = new HashSet<>();
        ProcessBuilder builder = new ProcessBuilder();

        // set up process
        builder.command(command, findBugsUIParam, findBugsPriority, jarPath);
        builder.directory(projectDirectory);

        // make sure findbugs is in process path
        Map<String, String> envs = builder.environment();
        envs.put("PATH", findBugsPath + File.pathSeparator + System.getenv("PATH"));

        Process p;
        try {
            p = builder.start();
        } catch (IOException e) {
            LOGGER.error("An error occurred when starting the findbugs process", e);
            e.printStackTrace();
            return results;
        }

        // store the results as they are found
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                Optional<CommitTD> optional = TechnicalDebtMapper.parseFindBugsOutput(line);
                optional.ifPresent(results::add);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred when processing findbugs output", e);
            e.printStackTrace();
            return results;
        }

        // wait for process to complete
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred when processing findbugs output", e);
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Returns all the JARs that contain the name of the project.
     *
     * @param projectFolder folder of the build files
     * @param projectName   the name of the project
     * @return a list of all the jars in the form of absolute paths
     * @throws IOException if the project folder does not exist
     */
    List<String> getProjectJars(File projectFolder, String projectName) {
        Path paths = Paths.get(projectFolder.getAbsolutePath());
        try {
            return Files.walk(paths).filter(Files::isRegularFile).filter(path -> isProjectJar(path, projectName))
                    .map(Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("An error occurred when searching for project JARs", e);
            return new ArrayList<>();
        }
    }

    boolean isProjectJar(Path path, String projectName) {
        String fileName = path.getFileName().toString();
        return fileName.contains(projectName) && isJarFile(fileName) && !isSourcesJar(fileName) && !isDocsJar(fileName)
                && !isTestsJar(fileName);
    }

    boolean isJarFile(String fileName) {
        int index = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return index > 0 && fileName.substring(index).equals(JAR_EXTENSION);
    }

    boolean isSourcesJar(String fileName) {
        return fileName.contains("sources");
    }

    boolean isDocsJar(String fileName) {
        return fileName.contains("doc");
    }

    boolean isTestsJar(String fileName) {
        return fileName.contains("test");
    }
}
