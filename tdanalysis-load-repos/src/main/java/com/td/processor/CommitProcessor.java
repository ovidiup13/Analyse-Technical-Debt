package com.td.processor;

import com.td.helpers.BuildHelper;
import com.td.models.CommitModel;
import com.td.models.RepositoryModel;
import com.td.helpers.VersionControlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Component
public class CommitProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    @Autowired
    private BuildHelper buildHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitProcessor.class);

    @Override
    public RepositoryModel process(RepositoryModel item) throws Exception {
        LOGGER.info(String.format("Processing commits for repository %s:%s", item.getAuthor(), item.getName()));

        File repoPath = item.getProjectFolder();
        try (VersionControlHelper versionControlHelper = new VersionControlHelper(repoPath)) {

            // set the commits of the repository
            item.setCommits(versionControlHelper.getCommits());

            // get the diff for each commit
            for (CommitModel commit : item.getCommits()) {
                commit.setDiff(versionControlHelper.getDiff(commit.getSha() + "^", commit.getSha()));
                versionControlHelper.checkoutRevision(commit.getSha());
                buildHelper.buildRepository(item);
            }

        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", item.getURI()), e);
            return null;
        }

        return item;
    }
}
