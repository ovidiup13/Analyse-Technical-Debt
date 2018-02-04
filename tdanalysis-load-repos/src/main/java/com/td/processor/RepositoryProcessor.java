package com.td.processor;

import com.td.models.RepositoryModel;
import com.td.helpers.VersionControlHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class RepositoryProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Override
    public RepositoryModel process(RepositoryModel item) {
        LOGGER.info(String.format("Reading repository info %s:%s", item.getAuthor(), item.getName()));

        File repoPath = new File(Paths.get(tempFolder, item.getName()).toString());
        try (VersionControlHelper versionControlHelper = repoPath.exists() ? new VersionControlHelper(repoPath) : new VersionControlHelper(item.getURI(), repoPath)) {
            item.setCommits(versionControlHelper.getCommits());
        } catch (IOException | GitAPIException e) {
            LOGGER.error(String.format("An error occurred when processing commits %s", item.getURI()), e);
            return null;
        }

        System.out.println(item.getCommits());
        return item;
    }

}
