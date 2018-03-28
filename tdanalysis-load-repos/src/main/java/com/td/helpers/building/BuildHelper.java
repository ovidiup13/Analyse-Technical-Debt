package com.td.helpers.building;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.td.models.BuildStatus;
import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BuildHelper {

    private static final Logger logger = LoggerFactory.getLogger(BuildHelper.class);
    protected final String BUILD_FAILURE_MESSAGE = "BUILD FAILURE";

    public abstract BuildStatus buildRepository(RepositoryModel repo);

    protected BuildStatus startBuildProcess(ProcessBuilder builder) {
        try {
            Process p = builder.start();
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

            return BuildStatus.SUCCESSFUL;
        } catch (IOException | InterruptedException e) {
            logger.error("An error occurred when building repository", e);
            return BuildStatus.FAILED;
        }
    }
}