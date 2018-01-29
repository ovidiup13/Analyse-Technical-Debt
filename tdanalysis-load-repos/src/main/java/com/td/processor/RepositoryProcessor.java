package com.td.processor;

import com.td.models.RepositoryModel;
import com.td.vcs.GitProcessor;
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
        File repoPath = new File(Paths.get(tempFolder, item.getName()).toString());
        try(GitProcessor gitProcessor = repoPath.exists() ? new GitProcessor(repoPath) : new GitProcessor(item.getURI(), repoPath)) {
            item.setCommits(gitProcessor.getCommits());
        } catch (IOException | GitAPIException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", item.getURI()), e);
            return null;
        }

        return item;
    }

}
