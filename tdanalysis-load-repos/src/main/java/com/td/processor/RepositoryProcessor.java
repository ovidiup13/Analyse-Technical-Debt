package com.td.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.td.helpers.VersionControlHelper;
import com.td.models.RepositoryModel;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RepositoryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    public RepositoryModel process(RepositoryModel item) {
        LOGGER.info(String.format("Reading repository info %s:%s", item.getAuthor(), item.getName()));

        File repoPath = new File(Paths.get(tempFolder, item.getName()).toString());
        try (VersionControlHelper ignored = repoPath.exists() ? new VersionControlHelper(repoPath)
                : new VersionControlHelper(item.getURI(), repoPath)) {
            item.setProjectFolder(repoPath);
        } catch (IOException | GitAPIException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", item.getURI()), e);
        }

        return item;
    }

}
