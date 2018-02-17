package com.td.processor;

import com.td.db.CommitRepository;
import com.td.helpers.BuildHelper;
import com.td.helpers.StaticAnalysisHelper;
import com.td.helpers.VersionControlHelper;
import com.td.models.BugModel;
import com.td.models.CommitModel;
import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommitProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    @Value("${findbugs.home.path}")
    private String findbugsPath;

    @Autowired
    private CommitRepository commitRepository;

    @Override
    public RepositoryModel process(RepositoryModel item) throws Exception {
        LOGGER.info(String.format("Processing commits for repository %s:%s", item.getAuthor(), item.getName()));

        BuildHelper buildHelper = new BuildHelper(javaHomePath, mavenHomePath);
        StaticAnalysisHelper staticAnalysisHelper = new StaticAnalysisHelper(findbugsPath);

        File repoPath = new File(Paths.get(tempFolder, item.getName()).toString());
        try (VersionControlHelper versionControlHelper = new VersionControlHelper(repoPath)) {

            List<CommitModel> commits = versionControlHelper.getCommits();

            // set the commits of the repository
            item.setCommits(commits.parallelStream().map(CommitModel::getSha).collect(Collectors.toList()));

            // get the diff for each commit
            for (CommitModel commit : commits) {

                // get diff
                commit.setDiff(versionControlHelper.getDiff(commit.getSha() + "^", commit.getSha()));

                // checkout revision
                versionControlHelper.checkoutRevision(commit.getSha());

                // build the revision
                buildHelper.buildRepository(item);

                // analyse for bugs
                List<BugModel> bugs = staticAnalysisHelper.executeAnalysis(item);
                commit.setBugs(bugs);

                // set the repository id
                commit.setRepositoryId(item.getId());

                // save the commit to db
                commitRepository.insert(commit);
            }

        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", item.getURI()), e);
        }

        return item;
    }
}
