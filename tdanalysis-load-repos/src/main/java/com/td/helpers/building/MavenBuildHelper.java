package com.td.helpers.building;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.td.models.BuildStatus;
import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MavenBuildHelper {

    private static final Logger logger = LoggerFactory.getLogger(MavenBuildHelper.class);
    protected final String BUILD_FAILURE_MESSAGE = "BUILD FAILURE";

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
        builder.directory(repo.getProjectFolder());

        logger.info(String.format("Starting build process for repository %s", repo.getName()));
        return startBuildProcess(builder);
    }

    protected BuildStatus startBuildProcess(ProcessBuilder builder) {
        try {
            Process p = builder.start();
            logger.info("Process builder started...");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
                if (line.contains(BUILD_FAILURE_MESSAGE)) {
                    p.destroy();
                    return BuildStatus.FAILED;
                }
            }
            p.waitFor();
            p.destroy();

            return p.exitValue() == 0 ? BuildStatus.SUCCESSFUL : BuildStatus.FAILED;
        } catch (IOException | InterruptedException e) {
            logger.error("An error occurred when building repository", e);
            e.printStackTrace();
            return BuildStatus.FAILED;
        }
    }

}
