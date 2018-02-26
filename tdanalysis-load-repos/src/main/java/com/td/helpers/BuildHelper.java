package com.td.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.td.models.BuildStatus;
import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class BuildHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildHelper.class);
    private static final String BUILD_FAILURE_MESSAGE = "BUILD FAILURE";

    private static final List<String> PERFORMANCE_FLAGS = new ArrayList<String>() {
        {
            add("-T");
            add("4");
        }
    };

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    public BuildHelper(String javaHomePath, String taskRunnerPath) {
        this.javaHomePath = javaHomePath;
        this.mavenHomePath = taskRunnerPath;
    }

    public BuildStatus buildRepository(RepositoryModel repositoryModel) {
        ProcessBuilder builder = new ProcessBuilder();
        List<String> commands = new ArrayList<>(Arrays.asList(repositoryModel.getBuildCommand().split(" ")));
        commands.addAll(PERFORMANCE_FLAGS);

        builder.command(commands);
        Map<String, String> envs = builder.environment();
        envs.put("JAVA_HOME", javaHomePath);
        envs.put("MAVEN_HOME", mavenHomePath);
        builder.directory(repositoryModel.getProjectFolder());

        // set initial build to successful
        BuildStatus status;

        Process p = null;
        try {
            p = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
                if (line.contains(BUILD_FAILURE_MESSAGE)) {
                    status = BuildStatus.FAILED;
                }
            }
            p.waitFor();
            p.destroy();

            status = BuildStatus.SUCCESSFUL;
        } catch (IOException | InterruptedException e) {
            LOGGER.error(String.format("An error occurred when building repository %s", repositoryModel.getName()), e);
            status = BuildStatus.FAILED;
        }

        return status;
    }

}
