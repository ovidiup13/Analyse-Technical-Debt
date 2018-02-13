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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticAnalysisHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticAnalysisHelper.class);

    private static final String JAR_EXTENSION = ".jar";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    //TODO: make sure this works on windows platform
    private static final String[] COMMANDS = new String[]{"findbugs.bat", "-textui"};

    // TODO: implement this method
    public List<BugModel> executeAnalysis(RepositoryModel repositoryModel, String findBugsPath) throws IOException {
        List<BugModel> results = new ArrayList<>();

        // TODO: make sure project is built
        List<String> projectJars = getProjectJars(repositoryModel.getProjectFolder(), repositoryModel.getName());

        String[] fullCommand = Stream.of(COMMANDS, projectJars.toArray()).flatMap(Stream::of).toArray(String[]::new);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("findbugs.bat", "-textui", projectJars.get(0), projectJars.get(1));
        Map<String, String> envs = builder.environment();
        envs.put("PATH", findBugsPath + File.pathSeparator + System.getenv("PATH"));
        builder.directory(repositoryModel.getProjectFolder());

        LOGGER.info("Starting static analysis for project {} on JARs {} and {}", repositoryModel.getName(), projectJars.get(0), projectJars.get(1));
        Process p = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            results.add(parseBug(line));
        }

        return results;
    }

    private BugModel parseBug(String line){

        BugModel bug = new BugModel();
        int index = line.indexOf(":");

        bug.setType(line.substring(0, index - 1));
        bug.setText(line.substring(index + 1));
        bug.setFullText(line);

        return bug;
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
        return Files
                .walk(Paths.get(projectFolder.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .filter(path -> isProjectJar(path, projectName))
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    private static boolean isProjectJar(Path path, String projectName) {
        String fileName = path.getFileName().toString();
        return fileName.contains(projectName) && isJarFile(fileName);
    }

    private static boolean isJarFile(String fileName) {
        int index = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return index > 0 && fileName.substring(index).equals(JAR_EXTENSION);
    }
}
