package com.td.helpers.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.td.models.BuildStatus;
import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MavenBuildHelper extends BuildHelper {

    private static final Logger logger = LoggerFactory.getLogger(MavenBuildHelper.class);

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    private static final List<String> PERFORMANCE_FLAGS = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("-T");
            add("2");
        }
    };

    public BuildStatus buildRepository(RepositoryModel repo) {
        ProcessBuilder builder = new ProcessBuilder();
        List<String> commands = new ArrayList<>(Arrays.asList(repo.getBuildCommand().split(" ")));
        commands.addAll(PERFORMANCE_FLAGS);

        builder.command(commands);
        Map<String, String> envs = builder.environment();
        envs.put("JAVA_HOME", javaHomePath);
        envs.put("MAVEN_HOME", mavenHomePath);
        builder.directory(repo.getProjectFolder());

        logger.info(String.format("Starting build process for repository %s", repo.getName()));
        return startBuildProcess(builder);
    }

}
