package com.td.helpers;

import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Component
public class BuildHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildHelper.class);

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    public BuildHelper(){}

    public void buildRepository(RepositoryModel repositoryModel) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();

        String[] commands = repositoryModel.getBuildCommand().split(" ");
        builder.command(commands);
        Map<String, String> envs = builder.environment();
        System.out.println(javaHomePath);
        System.out.println(mavenHomePath);
        envs.put("JAVA_HOME", javaHomePath);
        envs.put("MAVEN_HOME", mavenHomePath);
        builder.directory(repositoryModel.getProjectFolder());

        Process p = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.info(line);
        }
        p.waitFor();
    }

}
