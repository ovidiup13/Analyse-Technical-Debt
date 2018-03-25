package com.td.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.helpers.BuildHelper;
import com.td.helpers.GithubTrackerHelper;
import com.td.helpers.IssueTrackerHelper;
import com.td.helpers.JiraTrackerHelper;
import com.td.helpers.VersionControlHelper;
import com.td.helpers.analysis.FindBugsAnalysisHelper;
import com.td.helpers.analysis.StaticAnalysisHelper;
import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;
import com.td.models.TechnicalDebt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommitProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Value("${java.home.path}")
    private String javaHomePath;

    @Value("${maven.home.path}")
    private String mavenHomePath;

    @Value("${jira.username}")
    private String jiraUsername;

    @Value("${jira.password}")
    private String jiraPassword;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    @Qualifier("findBugsAnalysisHelper")
    private FindBugsAnalysisHelper findBugsAnalysisHelper;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    public List<CommitModel> process(RepositoryModel repositoryModel) throws Exception {
        LOGGER.info(String.format("Processing commits for repository %s:%s", repositoryModel.getAuthor(),
                repositoryModel.getName()));

        // helpers
        BuildHelper buildHelper = new BuildHelper(javaHomePath, mavenHomePath);
        IssueTrackerHelper issueTrackerHelper = getTrackerHelper(repositoryModel);

        File repoPath = new File(Paths.get(tempFolder, repositoryModel.getName()).toString());
        repositoryModel.setProjectFolder(repoPath);

        List<CommitModel> commits = null;

        String repositoryId = repositoryModel.getId();

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
                BuildStatus buildStatus = buildHelper.buildRepository(repositoryModel);

                // set build status
                commit.setBuildStatus(buildStatus);

                // analyse for bugs
                if (buildStatus.equals(BuildStatus.SUCCESSFUL)) {
                    TechnicalDebt td = findBugsAnalysisHelper.executeAnalysis(repositoryModel);
                    commit.setTechnicalDebt(td);
                }

                // get issues from commit description
                List<String> issueKeys = issueTrackerHelper.getKeys(commit.getMessage());

                List<IssueModel> issues = issueKeys.stream().map(issueTrackerHelper::getIssue)
                        .flatMap(o -> streamopt(o)).collect(Collectors.toList());
                issueRepository.save(issues);

                List<String> issueIds = issues.stream().map(issue -> issue.getIssueId()).collect(Collectors.toList());
                commit.setIssueIds(issueIds);

                // save the commit to db in case anything else breaks
                commitRepository.save(commit);
            }

            versionControlHelper.close();

        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred when processing repository %s", repositoryModel.getURI()), e);
        }

        return commits;
    }

    /**
     * Method that returns an instance of IssueTrackerHelper based on the type of issue tracker for each repository.
     * TODO: might be a good idea to put this into a factory object
     */
    private IssueTrackerHelper getTrackerHelper(RepositoryModel repository) throws IOException {
        if (repository.getIssueTrackerURI().contains("jira")) {
            return new JiraTrackerHelper(jiraUsername, jiraPassword, repository);
        } else {
            return new GithubTrackerHelper(githubUsername, githubToken, repository);
        }
    }

    /**
    * Turns an Optional<T> into a Stream<T> of length zero or one depending upon
    * whether a value is present.
    */
    static <T> Stream<T> streamopt(Optional<T> opt) {
        if (opt.isPresent())
            return Stream.of(opt.get());
        else
            return Stream.empty();
    }

}
