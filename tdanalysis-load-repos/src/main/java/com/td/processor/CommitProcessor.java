package com.td.processor;

import com.td.models.CommitModel;
import com.td.models.RepositoryModel;
import com.td.helpers.VersionControlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CommitProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Override
    public RepositoryModel process(RepositoryModel item) throws Exception {
        LOGGER.info(String.format("Processing commits for repository %s:%s", item.getAuthor(), item.getName()));

        File repoPath = new File(Paths.get(tempFolder, item.getName()).toString());
        try (VersionControlHelper versionControlHelper = new VersionControlHelper(repoPath)) {
            System.out.println(item.getName());
            System.out.println(item.getCommits());
            for (CommitModel commit : item.getCommits()) {
                LOGGER.info(commit.toString());
                commit.setDiff(versionControlHelper.getDiff(commit.getSha() + "^", commit.getSha()));
            }
        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", item.getURI()), e);
            return null;
        }

        return item;
    }
}
