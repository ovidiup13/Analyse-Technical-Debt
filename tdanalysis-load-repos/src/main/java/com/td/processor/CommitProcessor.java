package com.td.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.helpers.BuildHelper;
import com.td.helpers.IssueTrackerHelper;
import com.td.helpers.JiraTrackerHelper;
import com.td.helpers.StaticAnalysisHelper;
import com.td.helpers.VersionControlHelper;
import com.td.models.BugModel;
import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommitProcessor implements ItemProcessor<RepositoryModel, List<CommitModel>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    @Value("${findbugs.home.path}")
    private String findbugsPath;

    @Value("${jira.username}")
    private String jiraUsername;

    @Value("${jira.password}")
    private String jiraPassword;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public List<CommitModel> process(RepositoryModel respositoryModel) throws Exception {
        LOGGER.info(String.format("Processing commits for repository %s:%s", respositoryModel.getAuthor(),
                respositoryModel.getName()));

        // helpers
        BuildHelper buildHelper = new BuildHelper(javaHomePath, mavenHomePath);
        StaticAnalysisHelper staticAnalysisHelper = new StaticAnalysisHelper(findbugsPath);
        IssueTrackerHelper issueTrackerHelper = new JiraTrackerHelper(jiraUsername, jiraPassword, respositoryModel);

        File repoPath = new File(Paths.get(tempFolder, respositoryModel.getName()).toString());
        respositoryModel.setProjectFolder(repoPath);

        List<CommitModel> commits = null;

        String repositoryId = respositoryModel.getId();

        try (VersionControlHelper versionControlHelper = new VersionControlHelper(repoPath)) {

            commits = versionControlHelper.getCommits();

            // get the diff for each commit
            for (CommitModel commit : commits) {

                // set the repository id
                commit.setRepositoryId(repositoryId);

                // get diff
                commit.setDiff(versionControlHelper.getDiff(commit.getSha() + "^", commit.getSha()));

                // checkout revision
                versionControlHelper.checkoutRevision(commit.getSha());

                // build the revision
                // BuildStatus buildStatus = buildHelper.buildRepository(item);

                // set build status
                // commit.setBuildStatus(buildStatus);

                // analyse for bugs
                // if (buildStatus.equals(BuildStatus.SUCCESSFUL)) {
                //     List<BugModel> bugs = staticAnalysisHelper.executeAnalysis(item);
                //     commit.setBugs(bugs);
                // }

                List<String> issueKeys = issueTrackerHelper.getKeys(commit.getMessage());
                List<IssueModel> issues = issueKeys.stream().map(issueTrackerHelper::getIssue)
                        .collect(Collectors.toList());

                commit.setIssueIds(issueKeys);

                // save all issues found
                issueRepository.save(issues);

                // save the commit to db in case anything else breaks
                commitRepository.save(commit);
            }

            versionControlHelper.close();

        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", respositoryModel.getURI()),
                    e);
        }

        return commits;
    }
}
