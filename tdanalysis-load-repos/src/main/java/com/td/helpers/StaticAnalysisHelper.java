package com.td.helpers;

import com.td.models.RepositoryModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaticAnalysisHelper {

    private static final String JAR_EXTENSION = ".jar";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private static final String[] COMMANDS = new String[]{"spotbugs.bat", "-textui"};

    // TODO: implement this method
    public List<String> executeAnalysis(RepositoryModel repositoryModel) throws IOException {
        List<String> results = new ArrayList<>();

        // TODO: make sure project is built
        List<String> projectJars = getProjectJars(repositoryModel.getProjectFolder(), repositoryModel.getName());
        System.out.println(projectJars.toString());

//        String[] fullCommand = Stream.of(COMMANDS, projectJars.toArray()).flatMap(Stream::of).toArray(String[]::new);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("findbugs.bat", "-textui", projectJars.get(0));
        builder.directory(repositoryModel.getProjectFolder());

        Process p = builder.start();
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null) {
            results.add(line);
         }

        return results;
    }

    /**
     * Returns all the JARs that contain the name of the project.
     * @param projectFolder folder of the build files
     * @param projectName the name of the project
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
