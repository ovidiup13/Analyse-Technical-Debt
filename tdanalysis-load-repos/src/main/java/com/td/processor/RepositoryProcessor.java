package com.td.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.td.db.IssueRepository;
import com.td.helpers.VersionControlHelper;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RepositoryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryProcessor.class);
    private static final String tempFolder = Paths.get(System.getProperty("user.dir"), "tmp").toString();

    @Value("${jira.username}")
    private String jiraUsername;

    @Value("${jira.password}")
    private String jiraPassword;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommitProcessor commitProcessor;

    /**
     * Process the repositories in the file as follows:
     * 1. Clones them to local disk or reads them if they are not present.
     * 2. Process each commit one by one.
     */
    public void processRepositories(List<RepositoryModel> repositories) {
        repositories.stream().parallel().forEach(repo -> {
            Optional<VersionControlHelper> optVc = readOrCloneRepository(repo);
            if (!optVc.isPresent()) {
                return;
            }

            VersionControlHelper vch = optVc.get();
            IssueProcessor issueProcessor = createIssueProcessor(repo);

            // process commits
            vch.getCommitStream().forEachOrdered(commit -> {

                // checkout revision
                if (!vch.checkoutRevision(commit.getSha())) {
                    return;
                }

                // get issues
                List<IssueModel> issues = issueProcessor.getIssues(commit);
                commit.setIssueIds(issueProcessor.getIssueIds(issues));
                issueRepository.save(issues);

                // process commit
                commitProcessor.processCommit(commit, repo);
                commitProcessor.saveCommit(commit);
            });

            vch.close();
        });

    }

    /**
     * This method will try to clone a repository to the local disk. If the
     * repository already exists it will try to open it.
     * @return an {@link Optional} containing a {@link VersionControlHelper} if
     * successful or null otherwise.  
     */
    Optional<VersionControlHelper> readOrCloneRepository(RepositoryModel repo) {
        logger.info(String.format("Reading repository info %s:%s", repo.getAuthor(), repo.getName()));

        File repoPath = new File(Paths.get(tempFolder, repo.getName()).toString());
        VersionControlHelper versionControlHelper = null;

        try {
            versionControlHelper = repoPath.exists() ? new VersionControlHelper(repoPath)
                    : new VersionControlHelper(repo.getURI(), repoPath);
            repo.setProjectFolder(repoPath);
        } catch (IOException | GitAPIException e) {
            logger.error(String.format("An error occurred when processing repository %s", repo.getURI()), e);
        } catch (SecurityException e) {
            logger.error("Read cccess to the specified folder is restricted", e);
        }

        return Optional.ofNullable(versionControlHelper);
    }

    private IssueProcessor createIssueProcessor(RepositoryModel repo) {
        if (repo.getIssueTrackerURI().contains("jira")) {
            return new IssueProcessor(jiraUsername, jiraPassword, repo);
        } else {
            return new IssueProcessor(githubUsername, githubToken, repo);
        }
    }
}
